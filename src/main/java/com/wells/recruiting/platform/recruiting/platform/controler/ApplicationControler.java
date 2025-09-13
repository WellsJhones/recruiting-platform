// src/main/java/com/wells/recruiting/platform/recruiting/platform/controler/ApplicationControler.java
package com.wells.recruiting.platform.recruiting.platform.controler;

import com.wells.recruiting.platform.recruiting.platform.Application;
import com.wells.recruiting.platform.recruiting.platform.company.Employer;
import com.wells.recruiting.platform.recruiting.platform.dto.ApplicationDTO;
import com.wells.recruiting.platform.recruiting.platform.dto.JobDTO;
import com.wells.recruiting.platform.recruiting.platform.model.ApplicationStatus;
import com.wells.recruiting.platform.recruiting.platform.repository.ApplicationRepository;
import com.wells.recruiting.platform.recruiting.platform.repository.EmployerRepository;
import com.wells.recruiting.platform.recruiting.platform.repository.JobRepository;
import com.wells.recruiting.platform.recruiting.platform.security.TokenService;
import com.wells.recruiting.platform.recruiting.platform.job.Job;
import com.wells.recruiting.platform.recruiting.platform.user.User;
import com.wells.recruiting.platform.recruiting.platform.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/applications")
public class ApplicationControler {

    @Autowired
    private ApplicationRepository applicationRepository;

    @Autowired
    private JobRepository<Job, Long> jobRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private EmployerRepository employerRepository;


    // Apply to a job
    @PostMapping("/{jobId}")
    public ResponseEntity<?> applyToJob(
            @RequestHeader("Authorization") String token,
            @PathVariable String jobId
    ) {
        String email = tokenService.getEmailFromToken(token.replace("Bearer ", ""));
        User user = userRepository.findByEmail(email);

        if (user == null || !"jobseeker".equalsIgnoreCase(user.getRole())) {
            return ResponseEntity.status(403).body("Only jobseeker can apply to jobs");
        }

        Job job = jobRepository.findById(Long.parseLong(jobId)).orElse(null);

        if (job == null) {
            return ResponseEntity.status(404).body("Job not found");
        }

        Application existing = applicationRepository.findByJob__idAndApplicant__id(job.get_id(), user.get_id());


        if (existing != null) {
            return ResponseEntity.status(400).body("You have already applied to this job");
        }

        Application application = new Application();
        application.setJob(job);
        application.setApplicant(user);
//        application.setResume(user.getResume());

        applicationRepository.save(application);

        return ResponseEntity.status(201).body(application);
    }
    // src/main/java/com/wells/recruiting/platform/recruiting/platform/controler/ApplicationControler.java
    @GetMapping("/my")
    public ResponseEntity<?> getMyApplications(@RequestHeader("Authorization") String token) {
        String email = tokenService.getEmailFromToken(token.replace("Bearer ", ""));
        User user = userRepository.findByEmail(email);


        if (user == null) {
            return ResponseEntity.status(401).body("Invalid user");
        }

        List<Application> applications = applicationRepository.findByApplicant__id(user.get_id());
        List<ApplicationDTO> dtos = applications.stream().map(app -> {
            ApplicationDTO dto = new ApplicationDTO();
            dto.set_id(String.valueOf(app.get_id()));
            Map<String, Object> applicantMap = new HashMap<>();
            applicantMap.put("_id", String.valueOf(app.getApplicant().get_id()));
            applicantMap.put("name", app.getApplicant().getName());
            applicantMap.put("email", app.getApplicant().getEmail());
            applicantMap.put("avatar", app.getApplicant().getAvatar());
            dto.setApplicant(applicantMap);
            dto.setStatus(ApplicationStatus.valueOf(app.getStatus()));
            dto.setCreatedAt(app.getCreatedAt().toString());
            dto.setUpdatedAt(app.getUpdatedAt().toString());
            dto.set__v(0);

            if (app.getJob() != null) {
                JobDTO jobDto = new JobDTO();
                jobDto.set_id(String.valueOf(app.getJob().get_id()));
                jobDto.setTitle(app.getJob().getTitle());
                jobDto.setLocation(app.getJob().getLocation());
                jobDto.setType(app.getJob().getType());
                jobDto.setCompany(app.getJob().getEmployer() != null ? String.valueOf(app.getJob().getEmployer().get_id()) : null);
                dto.setJob(jobDto);
            }
            return dto;
        }).toList();

        return ResponseEntity.ok(dtos);
    }
    @GetMapping("/job/{id}")
    public ResponseEntity<?> getApplicantsForJob(
            @RequestHeader("Authorization") String token,
            @PathVariable String id
    ) {
        System.out.println("Received request to get applicants for job ID: " + id);
        String email = tokenService.getEmailFromToken(token.replace("Bearer ", ""));
        System.out.println(email);
        Employer employer = employerRepository.findByEmail(email);

        if (employer == null || !"employer".equalsIgnoreCase(employer.getRole())) {
            return ResponseEntity.status(403).body("Only employer can view applicants");
        }

        Job job = jobRepository.findById(Long.parseLong(id)).orElse(null);
        if (job == null) {
            return ResponseEntity.status(404).body("Job not found");
        }
        System.out.println("Employer from token: " + employer.get_id() + ", Job owner: " + (job.getEmployer() != null ? job.getEmployer().get_id() : "null"));

        if (job.getEmployer() == null || !job.getEmployer().get_id().equals(employer.get_id())) {
            return ResponseEntity.status(403).body("You do not own this job");
        }

        List<Application> applications = applicationRepository.findByJob__id(job.get_id());
        List<ApplicationDTO> dtos = applications.stream().map(app -> {
            ApplicationDTO dto = new ApplicationDTO();
            dto.set_id(String.valueOf(app.get_id()));

            Map<String, Object> applicantMap = new HashMap<>();
            applicantMap.put("_id", String.valueOf(app.getApplicant().get_id()));
            applicantMap.put("name", app.getApplicant().getName());
            applicantMap.put("email", app.getApplicant().getEmail());
            applicantMap.put("avatar", app.getApplicant().getAvatar());
            dto.setApplicant(applicantMap);

            dto.setStatus(
                    app.getStatus() != null
                            ? ApplicationStatus.valueOf(app.getStatus())
                            : ApplicationStatus.APPLIED // or your desired default
            );


            dto.setCreatedAt(app.getCreatedAt().toString());
            dto.setUpdatedAt(app.getUpdatedAt().toString());
            dto.set__v(0);

            if (app.getJob() != null) {
                JobDTO jobDto = new JobDTO();
                jobDto.set_id(String.valueOf(app.getJob().get_id()));
                jobDto.setTitle(app.getJob().getTitle());
                jobDto.setLocation(app.getJob().getLocation());
                jobDto.setType(app.getJob().getType());
                jobDto.setCompany(app.getJob().getEmployer() != null ? String.valueOf(app.getJob().getEmployer().get_id()) : null);
                dto.setJob(jobDto);
            }
            return dto;
        }).toList();



        return ResponseEntity.ok(dtos);
    }


}
