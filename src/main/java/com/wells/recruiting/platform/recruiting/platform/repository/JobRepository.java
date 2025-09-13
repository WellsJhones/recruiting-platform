package com.wells.recruiting.platform.recruiting.platform.repository;

import com.wells.recruiting.platform.recruiting.platform.job.Job;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface JobRepository extends JpaRepository<Job, Long> {
    List<Job> findByIsClosedFalse();

    List<Job> findTop5ByOrderByCreatedAtDesc();
}
