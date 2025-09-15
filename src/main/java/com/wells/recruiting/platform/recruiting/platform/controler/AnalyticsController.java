package com.wells.recruiting.platform.recruiting.platform.controler;
import com.wells.recruiting.platform.recruiting.platform.job.Application;
import com.wells.recruiting.platform.recruiting.platform.company.Employer;
import com.wells.recruiting.platform.recruiting.platform.job.Job;
import com.wells.recruiting.platform.recruiting.platform.model.Status;
import com.wells.recruiting.platform.recruiting.platform.repository.ApplicationRepository;
import com.wells.recruiting.platform.recruiting.platform.repository.JobRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.*;
import com.wells.recruiting.platform.recruiting.platform.security.TokenService;
import com.wells.recruiting.platform.recruiting.platform.repository.EmployerRepository;
import java.time.ZoneId;

@RestController
@RequestMapping("/api/analytics/overview")
public class AnalyticsController {
    @Autowired
    private JobRepository<Job, Long> jobRepository;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private EmployerRepository employerRepository;

    @Autowired
    private ApplicationRepository applicationRepository;

    @GetMapping
    public ResponseEntity<Object> getOverview(
            @RequestHeader(value = "Authorization", required = false) String token
    ) {
        String email = tokenService.getEmailFromToken(token.replace("Bearer ", ""));
        Employer owner = employerRepository.findByEmail(email);

        Map<String, Object> response = new HashMap<>();
        Map<String, Object> counts = new HashMap<>();

        // Count active jobs for the owner
        long totalActiveJobs = jobRepository.countByEmployer_IdAndIsClosed(owner.get_id(), false);
        counts.put("totalActiveJobs", totalActiveJobs);

        // Count applications for jobs owned by the owner
        long totalApplications = applicationRepository.countByJob_Employer_Id(owner.get_id());
        counts.put("totalApplications", totalApplications);

        // Count total hires (applications with ACCEPTED status for jobs owned by employer)
        long totalHires = applicationRepository.countByJob_Employer_IdAndStatus(owner.get_id(), Status.ACCEPTED);
        counts.put("totalHires", totalHires);

        // Fetch all jobs for the employer
        List<Job> jobs = jobRepository.findByEmployer_Id(owner.get_id());

        // Fetch all applications for jobs owned by the employer
        List<Application> allApplications = applicationRepository.findByJob_Employer_Id(owner.get_id());

        // Calculate one week and two weeks ago
        LocalDateTime oneWeekAgo = LocalDateTime.now().minusDays(7);
        LocalDateTime twoWeeksAgo = LocalDateTime.now().minusDays(14);
        Instant oneWeekAgoInstant = oneWeekAgo.atZone(ZoneId.systemDefault()).toInstant();
        Instant twoWeeksAgoInstant = twoWeeksAgo.atZone(ZoneId.systemDefault()).toInstant();

        // Count active jobs created in the previous week (between 1 and 2 weeks ago)
        long prevActiveJobs = jobs.stream()
                .filter(job -> job.getCreatedAt() != null
                        && job.getCreatedAt().isAfter(twoWeeksAgo)
                        && job.getCreatedAt().isBefore(oneWeekAgo))
                .count();

        long jobsCreatedLastWeek = jobs.stream()
                .filter(job -> job.getCreatedAt() != null && job.getCreatedAt().isAfter(oneWeekAgo))
                .count();

        double activeJobsTrendPercent;
        if (prevActiveJobs == 0) {
            activeJobsTrendPercent = totalActiveJobs > 0 ? 100.0 * totalActiveJobs : 0.0;
        } else {
            activeJobsTrendPercent = ((double)(totalActiveJobs - prevActiveJobs) / prevActiveJobs) * 100;
        }

        // Applications created in the last week
        long applicationsLastWeek = allApplications.stream()
                .filter(app -> app.getCreatedAt() != null && app.getCreatedAt().isAfter(oneWeekAgoInstant))
                .count();

        long applicationsPrevWeek = allApplications.stream()
                .filter(app -> app.getCreatedAt() != null
                        && app.getCreatedAt().isAfter(twoWeeksAgoInstant)
                        && app.getCreatedAt().isBefore(oneWeekAgoInstant))
                .count();

        double applicationsTrendPercent;
        if (applicationsPrevWeek == 0) {
            applicationsTrendPercent = applicationsLastWeek > 0 ? 100.0 * applicationsLastWeek : 0.0;
        } else {
            applicationsTrendPercent = ((double)(applicationsLastWeek - applicationsPrevWeek) / applicationsPrevWeek) * 100;
        }
        // Hires in the last week
        long hiresLastWeek = allApplications.stream()
                .filter(app -> app.getCreatedAt() != null
                        && app.getCreatedAt().isAfter(oneWeekAgoInstant)
                        && app.getStatus() == Status.ACCEPTED)
                .count();

// Hires in the previous week
        long hiresPrevWeek = allApplications.stream()
                .filter(app -> app.getCreatedAt() != null
                        && app.getCreatedAt().isAfter(twoWeeksAgoInstant)
                        && app.getCreatedAt().isBefore(oneWeekAgoInstant)
                        && app.getStatus() == Status.ACCEPTED)
                .count();

        double hiredTrend;
        if (hiresPrevWeek == 0) {
            hiredTrend = hiresLastWeek > 0 ? 100.0 * hiresLastWeek : 0.0;
        } else {
            hiredTrend = ((double)(hiresLastWeek - hiresPrevWeek) / hiresPrevWeek) * 100;
        }

        Map<String, Object> trends = new HashMap<>();
        trends.put("activeJobs", activeJobsTrendPercent);
        trends.put("applications", applicationsTrendPercent);
        trends.put("hired", hiredTrend);
        counts.put("trends", trends);

        Map<String, Object> data = new HashMap<>();

        List<Map<String, Object>> recentJobs = new ArrayList<>();
        for (Job job : jobs) {
            if (job.getEmployer() != null && job.getEmployer().get_id().equals(owner.get_id())) {
                recentJobs.add(Map.of(
                        "_id", job.get_id() != null ? job.get_id() : "",
                        "title", job.getTitle() != null ? job.getTitle() : "",
                        "location", job.getLocation() != null ? job.getLocation() : "",
                        "type", job.getType() != null ? job.getType() : "",
                        "isClosed", job.isClosed(),
                        "createdAt", job.getCreatedAt() != null ? job.getCreatedAt().toString() : ""
                ));
            }
        }
        data.put("recentJobs", recentJobs);

        // Fetch the 5 most recent applications for jobs owned by the employer
        List<Application> applications = applicationRepository.findTop5ByJob_Employer_IdOrderByCreatedAtDesc(owner.get_id());

        List<Map<String, Object>> recentApplications = new ArrayList<>();
        for (Application app : applications) {
            Map<String, Object> jobMap = Map.of(
                    "_id", app.getJob().get_id(),
                    "title", app.getJob().getTitle()
            );
            Map<String, Object> applicantMap = Map.of(
                    "_id", app.getApplicant().get_id(),
                    "name", app.getApplicant().getName(),
                    "email", app.getApplicant().getEmail(),
                    "avatar", app.getApplicant().getAvatar()
            );
            Map<String, Object> appMap = new HashMap<>();
            appMap.put("_id", app.get_id());
            appMap.put("job", jobMap);
            appMap.put("applicant", applicantMap);
            appMap.put("resume", app.getResume());
            appMap.put("status", app.getStatus() != null ? app.getStatus().name() : "UNKNOWN");
            appMap.put("createdAt", app.getCreatedAt().toString());
            appMap.put("updatedAt", app.getUpdatedAt().toString());
            appMap.put("__v", 0); // or use actual version if available
            recentApplications.add(appMap);
        }
        data.put("recentApplications", recentApplications);

        response.put("counts", counts);
        response.put("data", data);

        return ResponseEntity.ok(response);
    }
}
