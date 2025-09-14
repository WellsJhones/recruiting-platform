package com.wells.recruiting.platform.recruiting.platform.dto;

import lombok.Getter;
import lombok.Setter;

// src/main/java/com/wells/recruiting/platform/recruiting/platform/job/JobResponseDTO.java
@Getter
@Setter

public class JobResponseDTO {
    private String _id;
    private String title;
    private String description;
    private String requirements;
    private String location;
    private String type;
    private CompanyDTO company;
    private Integer salaryMin;
    private Integer salaryMax;
    private Boolean isClosed;
    private String createdAt;
    private String updatedAt;
    private Integer __v;
    private Boolean isSaved;
    private String applicationStatus;
    private Integer applicationCount;
}
