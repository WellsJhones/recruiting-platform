package com.wells.recruiting.platform.recruiting.platform.repository;

import com.wells.recruiting.platform.recruiting.platform.job.SaveJob;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SaveJobRepository extends JpaRepository<SaveJob, Long> {
    SaveJob findByJobAndJobseeker(Long jobId, Long jobseeker);

    List<SaveJob> findBy_id(Long jobseekerId);
}
