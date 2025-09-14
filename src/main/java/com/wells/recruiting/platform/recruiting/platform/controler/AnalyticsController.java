package com.wells.recruiting.platform.recruiting.platform.controler;
import com.wells.recruiting.platform.recruiting.platform.Application;
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

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;
import com.wells.recruiting.platform.recruiting.platform.security.TokenService;
import com.wells.recruiting.platform.recruiting.platform.repository.EmployerRepository;


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

        Date oneWeekAgo = Date.from(LocalDate.now().minusDays(7).atStartOfDay(ZoneId.systemDefault()).toInstant());
        long prevActiveJobs = jobRepository.countActiveJobsLastWeek(owner.get_id(), oneWeekAgo);

//        long prevApplications = applicationRepository.countApplicationsLastWeek(owner.get_id(), oneWeekAgo);
//        long prevHired = applicationRepository.countHiredLastWeek(owner.get_id(), Status.ACCEPTED, oneWeekAgo);


// Calculate trends (percentage change)
        long activeJobsTrend = totalActiveJobs - prevActiveJobs;
        double activeJobsTrendPercent;
        if (prevActiveJobs == 0) {
            activeJobsTrendPercent = totalActiveJobs > 0 ? 100.0 : 0.0;
        } else {
            activeJobsTrendPercent = ((double)(totalActiveJobs - prevActiveJobs) / prevActiveJobs) * 100;
        }


//        long applicationsTrend = totalApplications - prevApplications;
//        long hiredTrend = totalHires - prevHired;

        Map<String, Object> trends = new HashMap<>();
        trends.put("activeJobs", activeJobsTrend);
//        trends.put("applications", applicationsTrend);
//        trends.put("hired", hiredTrend);
        counts.put("trends", trends);
        Map<String, Object> data = new HashMap<>();

              List<Job> jobs = jobRepository.findTop5ByOrderByCreatedAtDesc();
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
