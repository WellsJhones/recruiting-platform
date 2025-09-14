package com.wells.recruiting.platform.recruiting.platform.repository;// src/main/java/com/wells/recruiting/platform/recruiting/platform/repository/ApplicationRepository.java
import com.wells.recruiting.platform.recruiting.platform.Application;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ApplicationRepository extends JpaRepository<Application, Long> {
    Application findByJob__idAndApplicant__id(Long jobId, Long applicantId);

    List<Application> findByApplicant__id(Long applicantId);

    List<Application> findByJob__id(Long jobId);


    int countByJob__id(Long id);
}