package com.wells.recruiting.platform.recruiting.platform.controler;

import com.wells.recruiting.platform.recruiting.platform.Application;
import com.wells.recruiting.platform.recruiting.platform.company.Employer;
import com.wells.recruiting.platform.recruiting.platform.dto.ApplicationDTO;
import com.wells.recruiting.platform.recruiting.platform.dto.JobDTO;
import com.wells.recruiting.platform.recruiting.platform.model.Status;
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
            @PathVariable String jobId) {
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
        application.setStatus(Status.APPLIED);
        applicationRepository.save(application);

        // Update application count
        Integer count = job.getApplicationCount();
        job.setApplicationCount(count == null ? 1 : count + 1);
        jobRepository.save(job);

        // Return application and applicationCount
        Map<String, Object> response = new HashMap<>();
        response.put("application", application);
        response.put("applicationCount", job.getApplicationCount());
        return ResponseEntity.status(201).body(response);
    }

    // Get applications for the logged-in user
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
            applicantMap.put("resume", app.getApplicant().getResume());
            dto.setApplicant(applicantMap);
            dto.setStatus(app.getStatus());
            dto.setCreatedAt(app.getCreatedAt().toString());
            dto.setUpdatedAt(app.getUpdatedAt().toString());
            dto.set__v(0);

            if (app.getJob() != null) {
                JobDTO jobDto = new JobDTO();
                jobDto.set_id(String.valueOf(app.getJob().get_id()));
                jobDto.setTitle(app.getJob().getTitle());
                jobDto.setLocation(app.getJob().getLocation());
                jobDto.setType(app.getJob().getType());
                jobDto.setCompany(
                        app.getJob().getEmployer() != null ? String.valueOf(app.getJob().getEmployer().get_id())
                                : null);
                jobDto.setApplicationCount(app.getJob().getApplicationCount());

                dto.setJob(jobDto);
            }
            return dto;
        }).toList();

        return ResponseEntity.ok(dtos);
    }

    // Get applicants for a specific job (employer only)
    @GetMapping("/job/{id}")
    public ResponseEntity<?> getApplicantsForJob(
            @RequestHeader("Authorization") String token,
            @PathVariable String id) {
        String email = tokenService.getEmailFromToken(token.replace("Bearer ", ""));
        Employer employer = employerRepository.findByEmail(email);

        if (employer == null || !"employer".equalsIgnoreCase(employer.getRole())) {
            return ResponseEntity.status(403).body("Only employer can view applicants");
        }

        Job job = jobRepository.findById(Long.parseLong(id)).orElse(null);
        if (job == null) {
            return ResponseEntity.status(404).body("Job not found");
        }

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
            applicantMap.put("resume", app.getApplicant().getResume());
            dto.setApplicant(applicantMap);

            dto.setStatus(
                    app.getStatus() != null
                            ? app.getStatus()
                            : Status.APPLIED);

            dto.setCreatedAt(app.getCreatedAt().toString());
            dto.setUpdatedAt(app.getUpdatedAt().toString());
            dto.set__v(0);

            if (app.getJob() != null) {
                JobDTO jobDto = new JobDTO();
                jobDto.set_id(String.valueOf(app.getJob().get_id()));
                jobDto.setTitle(app.getJob().getTitle());
                jobDto.setLocation(app.getJob().getLocation());
                jobDto.setType(app.getJob().getType());
                jobDto.setCompany(
                        app.getJob().getEmployer() != null ? String.valueOf(app.getJob().getEmployer().get_id())
                                : null);
                jobDto.setApplicationCount(app.getJob().getApplicationCount());
                dto.setJob(jobDto);
            }
            return dto;
        }).toList();

        return ResponseEntity.ok(dtos);
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<?> updateApplicationStatus(
            @RequestHeader("Authorization") String token,
            @PathVariable String id,
            @RequestBody Map<String, String> payload) {
        String email = tokenService.getEmailFromToken(token.replace("Bearer ", ""));
        Employer employer = employerRepository.findByEmail(email);

        if (employer == null || !"employer".equalsIgnoreCase(employer.getRole())) {
            return ResponseEntity.status(403).body("Only employer can update status");
        }

        Application application = applicationRepository.findById(Long.parseLong(id)).orElse(null);
        if (application == null) {
            return ResponseEntity.status(404).body("Application not found");
        }

        if (application.getJob() == null || !application.getJob().getEmployer().get_id().equals(employer.get_id())) {
            return ResponseEntity.status(403).body("You do not own this job");
        }

        String newStatus = payload.get("status");
        if (newStatus == null) {
            return ResponseEntity.badRequest().body("Missing status");
        }
        try {
            application.setStatus(Status.valueOf(newStatus.toUpperCase().replace(" ", "_")));

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Invalid status value");
        }
        applicationRepository.save(application);

        return ResponseEntity.ok("Status updated");
    }

}
