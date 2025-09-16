// src/main/java/com/wells/recruiting/platform/recruiting/platform/controler/JobController.java
package com.wells.recruiting.platform.recruiting.platform.controler;
import com.wells.recruiting.platform.recruiting.platform.repository.*;


import com.wells.recruiting.platform.recruiting.platform.dto.JobRequest;
import com.wells.recruiting.platform.recruiting.platform.dto.JobResponseDTO;
import com.wells.recruiting.platform.recruiting.platform.dto.CompanyDTO;
import com.wells.recruiting.platform.recruiting.platform.job.Job;
import com.wells.recruiting.platform.recruiting.platform.security.TokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import com.wells.recruiting.platform.recruiting.platform.user.User;
import com.wells.recruiting.platform.recruiting.platform.Application;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.transaction.annotation.Transactional;


import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/jobs")
public class JobController {

    @Autowired
    private JobRepository<Job, Long> jobRepository;
    @Autowired
    private EmployerRepository employerRepository;
    @Autowired
    private TokenService tokenService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ApplicationRepository applicationRepository;
    @Autowired
    private SaveJobRepository saveJobRepository;



    @PostMapping
    public ResponseEntity<JobResponseDTO> createJob(@RequestBody JobRequest jobRequest, @RequestHeader("Authorization") String token) {
        String email = tokenService.getEmailFromToken(token.replace("Bearer ", ""));
        var employer = employerRepository.findByEmail(email);
        if (employer == null) {
            return ResponseEntity.status(403).build();
        }
        Job job = new Job();
        job.setTitle(jobRequest.title);
        job.setDescription(jobRequest.description);
        job.setRequirements(jobRequest.requirements);
        job.setLocation(jobRequest.location);
        job.setCategory(jobRequest.category);
        job.setType(jobRequest.type);
        job.setSalaryMin(jobRequest.salaryMin);
        job.setSalaryMax(jobRequest.salaryMax);
        job.setEmployer(employer);
        jobRepository.save(job);
        return ResponseEntity.ok(mapToResponseDTO(job));
    }


    @GetMapping
    @PreAuthorize("hasRole('jobseeker')")
    public ResponseEntity<List<JobResponseDTO>> getOpenJobs(@RequestHeader("Authorization") String token) {
        String email = tokenService.getEmailFromToken(token.replace("Bearer ", ""));
        User user = userRepository.findByEmail(email);
        List<Job> jobs = jobRepository.findByIsClosedFalse();
        List<JobResponseDTO> jobDTOs = jobs.stream()
                .map(job -> mapToResponseDTO(job, user))
                .collect(Collectors.toList());
        return ResponseEntity.ok(jobDTOs);
    }


    // Java
    private JobResponseDTO mapToResponseDTO(Job job, User user) {
        JobResponseDTO dto = new JobResponseDTO();
        dto.set_id(job.get_id().toString());
        dto.setTitle(job.getTitle());
        dto.setDescription(job.getDescription());
        dto.setRequirements(job.getRequirements());
        dto.setLocation(job.getLocation());
        dto.setType(job.getType());
        dto.setSalaryMin(job.getSalaryMin());
        dto.setSalaryMax(job.getSalaryMax());
        dto.setIsClosed(job.getIsClosed());

        // Set isSaved based on user
        boolean isSaved = false;
        if (user != null) {
            isSaved = saveJobRepository.existsByJobseekerAndJob(user.get_id(), job.get_id());

        }
        dto.setIsSaved(isSaved);

        dto.setCategory(job.getCategory());
        dto.setCreatedAt(job.getCreatedAt() != null ? job.getCreatedAt().toString() : null);
        dto.setUpdatedAt(job.getUpdatedAt() != null ? job.getUpdatedAt().toString() : null);

        CompanyDTO company = new CompanyDTO();
        company.set_id(job.getEmployer().getId().toString());
        company.setName(job.getEmployer().getName());
        company.setCompanyLogo(job.getEmployer().getCompanyLogo());
        company.setCompanyName(job.getEmployer().getCompanyName());
        dto.setCompany(company);

        int count = applicationRepository.countByJob__id(job.get_id());
        dto.setApplicationCount(count);

        Application app = applicationRepository.findByJob__idAndApplicant__id(job.get_id(), user != null ? user.get_id() : null);
        String applicationStatus = null;
        if (app != null && app.getStatus() != null) {
            applicationStatus = app.getStatus().name().replace("_", " ");
        }
        dto.setApplicationStatus(applicationStatus);

        return dto;
    }

    private JobResponseDTO mapToResponseDTO(Job job) {
        return mapToResponseDTO(job, null);
    }
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('jobseeker')")
    public ResponseEntity<JobResponseDTO> getJobById(
            @PathVariable("id") Long id,
            @RequestHeader("Authorization") String token
    ) {
        Job job = jobRepository.findById(id).orElse(null);
        if (job == null) {
            return ResponseEntity.notFound().build();
        }
        String email = tokenService.getEmailFromToken(token.replace("Bearer ", ""));
        User user = userRepository.findByEmail(email);
        return ResponseEntity.ok(mapToResponseDTO(job, user));
    }



    @PutMapping("/{id}")
    @PreAuthorize("hasRole('EMPLOYER')")
    public ResponseEntity<JobResponseDTO> updateJob(
            @PathVariable("id") Long id,
            @RequestBody JobRequest jobRequest,
            @RequestHeader("Authorization") String token) {
        String email = tokenService.getEmailFromToken(token.replace("Bearer ", ""));
        var employer = employerRepository.findByEmail(email);
        if (employer == null) {
            return ResponseEntity.status(403).build();
        }
        Job job = jobRepository.findById(id).orElse(null);
        if (job == null) {
            return ResponseEntity.notFound().build();
        }
        if (!job.getEmployer().getId().equals(employer.getId())) {
            return ResponseEntity.status(403).build();
        }
        job.setTitle(jobRequest.title);
        job.setDescription(jobRequest.description);
        job.setRequirements(jobRequest.requirements);
        job.setLocation(jobRequest.location);
        job.setCategory(jobRequest.category);
        job.setType(jobRequest.type);
        job.setSalaryMin(jobRequest.salaryMin);
        job.setSalaryMax(jobRequest.salaryMax);
        jobRepository.save(job);
        return ResponseEntity.ok(mapToResponseDTO(job));
    }


    @GetMapping("/get-jobs-employer")
    @PreAuthorize("hasRole('EMPLOYER')")
    public ResponseEntity<List<JobResponseDTO>> getJobsByEmployerToken(@RequestHeader("Authorization") String token) {
        String email = tokenService.getEmailFromToken(token.replace("Bearer ", ""));
        var employer = employerRepository.findByEmail(email);
        if (employer == null) {
            return ResponseEntity.status(403).build();
        }
        List<Job> jobs = jobRepository.findByEmployerId(employer.getId());
        List<JobResponseDTO> jobDTOs = jobs.stream()
                .map(job -> {
                    // For employer endpoints
                    JobResponseDTO dto = mapToResponseDTO(job); // uses the overload, passes null for user

                    int count = applicationRepository.countByJob__id(job.get_id());
                    dto.setApplicationCount(count); // Add this setter to your DTO
                    return dto;
                })
                .collect(Collectors.toList());
        return ResponseEntity.ok(jobDTOs);
    }


    @PutMapping("/{id}/toggle-close")
    @PreAuthorize("hasRole('EMPLOYER')")
    public ResponseEntity<JobResponseDTO> toggleJobClosed(
            @PathVariable("id") Long id,
            @RequestHeader("Authorization") String token) {
        String email = tokenService.getEmailFromToken(token.replace("Bearer ", ""));
        var employer = employerRepository.findByEmail(email);
        if (employer == null) {
            return ResponseEntity.status(403).build();
        }
        Job job = jobRepository.findById(id).orElse(null);
        if (job == null) {
            return ResponseEntity.notFound().build();
        }
        if (!job.getEmployer().getId().equals(employer.getId())) {
            return ResponseEntity.status(403).build();
        }
        job.setIsClosed(!job.getIsClosed()); // Toggle the closed status
        jobRepository.save(job);
        return ResponseEntity.ok(mapToResponseDTO(job));
    }


    // src/main/java/com/wells/recruiting/platform/recruiting/platform/controler/JobController.java


    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('EMPLOYER')")
    @Transactional
    public ResponseEntity<Void> deleteJob(
            @PathVariable("id") Long id,
            @RequestHeader("Authorization") String token) {
        String email = tokenService.getEmailFromToken(token.replace("Bearer ", ""));
        var employer = employerRepository.findByEmail(email);
        if (employer == null) {
            return ResponseEntity.status(403).build();
        }
        Job job = jobRepository.findById(id).orElse(null);
        if (job == null) {
            return ResponseEntity.notFound().build();
        }
        if (!job.getEmployer().getId().equals(employer.getId())) {
            return ResponseEntity.status(403).build();
        }
        // Delete related applications first
        applicationRepository.deleteByJob__id(job.get_id());
        jobRepository.delete(job);
        return ResponseEntity.noContent().build();
    }



}
