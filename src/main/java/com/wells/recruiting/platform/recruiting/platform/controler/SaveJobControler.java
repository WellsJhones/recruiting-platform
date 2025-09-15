package com.wells.recruiting.platform.recruiting.platform.controler;

import com.wells.recruiting.platform.recruiting.platform.company.Employer;
import com.wells.recruiting.platform.recruiting.platform.job.Job;
import com.wells.recruiting.platform.recruiting.platform.job.SaveJob;
import com.wells.recruiting.platform.recruiting.platform.repository.JobRepository;
import com.wells.recruiting.platform.recruiting.platform.repository.SaveJobRepository;
import com.wells.recruiting.platform.recruiting.platform.repository.UserRepository;
import com.wells.recruiting.platform.recruiting.platform.security.TokenService;
import com.wells.recruiting.platform.recruiting.platform.user.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@RestController
@RequestMapping("/api/saved-jobs")
public class SaveJobControler {

    @Autowired
    private SaveJobRepository savejobRepository;

    @Autowired
    private TokenService tokenService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private JobRepository jobRepository;


    @PostMapping("/{jobId}")
    public ResponseEntity<?> saveJob(
            @PathVariable String jobId,
            @RequestHeader("Authorization") String authHeader) {

        String token = authHeader.replace("Bearer ", "");
        String email = tokenService.extractEmail(token); // Implement this in your TokenService

        // Query userRepository for jobseeker id
        User user = userRepository.findByEmail(email);
        String jobseekerId = String.valueOf(user.get_id());

        System.out.println("Received: jobseeker=" + jobseekerId + ", job=" + jobId);

        try {
            SaveJob exists = savejobRepository.findByJobAndJobseeker(Long.valueOf(jobId), Long.valueOf(jobseekerId));
            if (exists != null) {
                return ResponseEntity.status(400).body(Map.of("message", "Job already saved"));
            }
            SaveJob savejob = new SaveJob();
            savejob.setJob(Long.valueOf(jobId));
            savejob.setJobseeker(Long.valueOf(jobseekerId));
            savejob.setCreatedAt(LocalDateTime.now());
            savejob.setUpdatedAt(LocalDateTime.now());
            savejob.set__v(0);
            SaveJob saved = savejobRepository.save(savejob);
            return ResponseEntity.status(201).body(saved);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }
    @GetMapping("/my")
    public ResponseEntity<?> getMySavedJobs(@RequestHeader("Authorization") String authHeader) {
        String token = authHeader.replace("Bearer ", "");
        String email = tokenService.extractEmail(token);
        User user = userRepository.findByEmail(email);
        Long jobseekerId = user.get_id();

        // Fetch saved jobs for this user
        List<SaveJob> savedJobs = savejobRepository.findByJobseeker(jobseekerId);

        // Map each SaveJob to the required response format
        List<Map<String, Object>> response = savedJobs.stream().map(saveJob -> {
            // Fetch job details
            Job job = (Job) jobRepository.findById(saveJob.getJob()).orElse(null);

            if (job == null) return null;
            Employer employer = job.getEmployer(); // Replace with your actual getter

            Map<String, Object> companyMap = new HashMap<>();
            if (employer.get_id() != null) companyMap.put("_id", employer.get_id());
            if (employer.getName() != null) companyMap.put("name", employer.getName());
            if (employer.getCompanyName() != null) companyMap.put("companyName", employer.getCompanyName());
            if (employer.getCompanyLogo() != null) companyMap.put("companyLogo", employer.getCompanyLogo());


            // Build job details map (add company info as needed)
            Map<String, Object> jobMap = new HashMap<>();
            jobMap.put("_id", job.get_id());
            jobMap.put("title", job.getTitle());
            jobMap.put("description", job.getDescription());
            jobMap.put("requirements", job.getRequirements());
            jobMap.put("location", job.getLocation());
            jobMap.put("category", job.getCategory());
            jobMap.put("type", job.getType());
            jobMap.put("salaryMin", job.getSalaryMin());
            jobMap.put("salaryMax", job.getSalaryMax());
            jobMap.put("isClosed", job.getIsClosed());
            jobMap.put("createdAt", job.getCreatedAt());
            jobMap.put("updatedAt", job.getUpdatedAt());
            jobMap.put("company", companyMap);;

            return Map.of(
                    "_id", saveJob.get_id(),
                    "jobseeker", saveJob.getJobseeker(),
                    "job", jobMap,
                    "createdAt", saveJob.getCreatedAt(),
                    "updatedAt", saveJob.getUpdatedAt(),
                    "__v", saveJob.get__v()
            );
        }).filter(Objects::nonNull).toList();

        return ResponseEntity.ok(response);
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<?> unsaveJobByJobId(
            @PathVariable Long id,
            @RequestHeader("Authorization") String authHeader) {

        String token = authHeader.replace("Bearer ", "");
        String email = tokenService.extractEmail(token);
        User user = userRepository.findByEmail(email);
        Long jobseekerId = user.get_id();

        SaveJob saveJob = savejobRepository.findByJobAndJobseeker(id, jobseekerId);
        if (saveJob == null) {
            return ResponseEntity.status(404).body(Map.of("error", "Saved job not found"));
        }
        savejobRepository.deleteById(saveJob.get_id());
        return ResponseEntity.ok(Map.of("message", "Job removed from saved list"));
    }


}