package com.wells.recruiting.platform.recruiting.platform.controler;
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



        List<Map<String, Object>> recentApplications = new ArrayList<>();
        recentApplications.add(Map.of(
                "_id", "68b7af5038e3bafa499e0a45",
                "job", Map.of("_id", "68ac8fe36c27e14bf58fa005", "title", "Backend Developer"),
                "applicant", Map.of("_id", "68b5ae4a383aa37aee284cc5", "name", "Loko", "email", "badeco@brabo.com", "avatar", "http://129.148.29.122:8000/uploads/1756861531662-132031351_p0.png"),
                "resume", "http://129.148.29.122:8000/uploads/1756868389418-Screenshot_20250805_155432_Firefox.jpg",
                "status", "Applied",
                "createdAt", "2025-09-03T03:00:32.026Z",
                "updatedAt", "2025-09-03T03:00:32.026Z",
                "__v", 0
        ));
        recentApplications.add(Map.of(
                "_id", "68b612ba38e3bafa499e02c5",
                "job", Map.of("_id", "68ac69da7207f11e697bb0f9", "title", "Frontend Developer (React JS)"),
                "applicant", Map.of("_id", "68b5ae4a383aa37aee284cc5", "name", "Loko", "email", "badeco@brabo.com", "avatar", "http://129.148.29.122:8000/uploads/1756861531662-132031351_p0.png"),
                "status", "Applied",
                "createdAt", "2025-09-01T21:40:10.152Z",
                "updatedAt", "2025-09-01T21:40:10.152Z",
                "__v", 0
        ));
        recentApplications.add(Map.of(
                "_id", "68acb0691eb04677ea435bd9",
                "job", Map.of("_id", "68ac69da7207f11e697bb0f9", "title", "Frontend Developer (React JS)"),
                "applicant", Map.of("_id", "68aca548ec6f11085b8c9cbb", "name", "Jhon", "email", "Jhon@example.com", "avatar", "http://localhost:8000/uploads/1756144936811-Captura de tela 2025-08-11 222435.png"),
                "status", "Rejected",
                "createdAt", "2025-08-25T18:50:17.741Z",
                "updatedAt", "2025-08-25T19:20:57.100Z",
                "__v", 0
        ));
        recentApplications.add(Map.of(
                "_id", "68aca5b82a306a6c8575407e",
                "job", Map.of("_id", "68ac8fe36c27e14bf58fa005", "title", "Backend Developer"),
                "applicant", Map.of("_id", "68aca548ec6f11085b8c9cbb", "name", "Jhon", "email", "Jhon@example.com", "avatar", "http://localhost:8000/uploads/1756144936811-Captura de tela 2025-08-11 222435.png"),
                "status", "Applied",
                "createdAt", "2025-08-25T18:04:40.783Z",
                "updatedAt", "2025-08-25T18:04:40.783Z",
                "__v", 0
        ));
        data.put("recentApplications", recentApplications);

        response.put("counts", counts);
        response.put("data", data);

        return ResponseEntity.ok(response);
    }
}
