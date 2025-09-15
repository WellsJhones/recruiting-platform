package com.wells.recruiting.platform.recruiting.platform.controler;

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
import java.util.Map;

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
}