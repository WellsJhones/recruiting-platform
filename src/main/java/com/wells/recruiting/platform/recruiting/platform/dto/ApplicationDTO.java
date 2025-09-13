package com.wells.recruiting.platform.recruiting.platform.dto;

import com.wells.recruiting.platform.recruiting.platform.model.ApplicationStatus;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Getter;
import lombok.Setter;
import java.util.Map;

@Getter
@Setter
public class ApplicationDTO {
    private String _id;
    private JobDTO job;
    private Map<String, Object> applicant; // <-- Replace String with Map
    private String createdAt;
    private String updatedAt;
    @Enumerated(EnumType.STRING)
    private ApplicationStatus status = ApplicationStatus.APPLIED;


    private int __v = 0; // Optional, for versioning
}
