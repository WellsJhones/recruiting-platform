// src/main/java/com/wells/recruiting/platform/recruiting/platform/controler/JobController.java
package com.wells.recruiting.platform.recruiting.platform.controler;

import com.wells.recruiting.platform.recruiting.platform.dto.JobRequest;
import com.wells.recruiting.platform.recruiting.platform.job.Job;
import com.wells.recruiting.platform.recruiting.platform.repository.JobRepository;
import com.wells.recruiting.platform.recruiting.platform.repository.EmployerRepository;
import com.wells.recruiting.platform.recruiting.platform.security.TokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/jobs")
public class JobController {

    @Autowired
    private JobRepository jobRepository;
    @Autowired
    private EmployerRepository employerRepository;
    @Autowired
    private TokenService tokenService;

    @PostMapping
    public ResponseEntity<?> createJob(@RequestBody JobRequest jobRequest, @RequestHeader("Authorization") String token) {
        String email = tokenService.getEmailFromToken(token.replace("Bearer ", ""));
        var employer = employerRepository.findByEmail(email);
        if (employer == null) {
            return ResponseEntity.status(403).body("Only employers can post jobs");
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
        return ResponseEntity.ok(job);
    }


    @GetMapping
    @PreAuthorize("hasRole('USER')")
    public List<Job> getOpenJobs() {
        return jobRepository.findByIsClosedFalse();
    }

}
