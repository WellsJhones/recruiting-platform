// src/main/java/com/wells/recruiting/platform/recruiting/platform/controler/JobController.java
package com.wells.recruiting.platform.recruiting.platform.controler;

import com.wells.recruiting.platform.recruiting.platform.dto.JobRequest;
import com.wells.recruiting.platform.recruiting.platform.dto.JobResponseDTO;
import com.wells.recruiting.platform.recruiting.platform.dto.CompanyDTO;
import com.wells.recruiting.platform.recruiting.platform.job.Job;
import com.wells.recruiting.platform.recruiting.platform.repository.JobRepository;
import com.wells.recruiting.platform.recruiting.platform.repository.EmployerRepository;
import com.wells.recruiting.platform.recruiting.platform.security.TokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

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
    @PreAuthorize("hasRole('USER')")
    public List<JobResponseDTO> getOpenJobs() {
        return jobRepository.findByIsClosedFalse()
                .stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    private JobResponseDTO mapToResponseDTO(Job job) {
        JobResponseDTO dto = new JobResponseDTO();
        dto.set_id(job.get_id().toString()); // Use correct getter for ID
        dto.setTitle(job.getTitle());
        dto.setDescription(job.getDescription());
        dto.setRequirements(job.getRequirements());
        dto.setLocation(job.getLocation());
        dto.setType(job.getType());
        dto.setSalaryMin(job.getSalaryMin());
        dto.setSalaryMax(job.getSalaryMax());
        dto.setIsClosed(job.getIsClosed());
        dto.setIsSaved(job.getIsSaved());
        dto.setApplicationStatus(job.getApplicationStatus());
        dto.setCreatedAt(job.getCreatedAt() != null ? job.getCreatedAt().toString() : null);
        dto.setUpdatedAt(job.getUpdatedAt() != null ? job.getUpdatedAt().toString() : null);

        CompanyDTO company = new CompanyDTO();
        company.set_id(job.getEmployer().getId().toString()); // Use correct getter for ID
        company.setName(job.getEmployer().getName());
        company.setCompanyLogo(job.getEmployer().getCompanyLogo());
        company.setCompanyName(job.getEmployer().getCompanyName());
        dto.setCompany(company);

        return dto;
    }
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<JobResponseDTO> getJobById(@PathVariable("id") Long id) {
        Job job = jobRepository.findById(id).orElse(null);
        if (job == null) {
            return ResponseEntity.notFound().build();
        }
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
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(jobDTOs);
    }



}
