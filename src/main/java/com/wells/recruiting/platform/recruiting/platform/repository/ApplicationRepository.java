package com.wells.recruiting.platform.recruiting.platform.repository;// src/main/java/com/wells/recruiting/platform/recruiting/platform/repository/ApplicationRepository.java
import com.wells.recruiting.platform.recruiting.platform.Application;
import com.wells.recruiting.platform.recruiting.platform.model.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;

public interface ApplicationRepository extends JpaRepository<Application, Long> {
    Application findByJob__idAndApplicant__id(Long jobId, Long applicantId);

    List<Application> findByApplicant__id(Long applicantId);

    List<Application> findByJob__id(Long jobId);


    int countByJob__id(Long id);

    long countByJob_Employer_IdAndStatus(Long id, Status status);

    long countByJob_Employer_Id(Long id);

    }