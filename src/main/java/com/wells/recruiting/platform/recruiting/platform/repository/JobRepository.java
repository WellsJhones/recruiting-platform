package com.wells.recruiting.platform.recruiting.platform.repository;

import com.wells.recruiting.platform.recruiting.platform.job.Job;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

public interface JobRepository<A, L extends Number> extends JpaRepository<Job, Long> {
    List<Job> findByIsClosedFalse();

    List<Job> findTop5ByOrderByCreatedAtDesc();

    List<Job> findByEmployerId(Long id);

    L countByEmployer_IdAndIsClosed(L id, boolean b);
    List<Job> findByEmployer_Id(Long employerId);

    default long countActiveJobsLastWeek(Long employerId, Date startDate) {
        List<Job> jobs = findByEmployer_Id(employerId);
        LocalDateTime startDateTime = startDate.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();
        return jobs.stream()
                .filter(job -> !job.isClosed() && job.getCreatedAt().isAfter(startDateTime))
                .count();
    }

}

