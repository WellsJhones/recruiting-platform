// src/main/java/com/wells/recruiting/platform/recruiting/platform/dto/JobRequest.java
package com.wells.recruiting.platform.recruiting.platform.dto;

import java.time.LocalDateTime;

public class JobRequest {
    public String title;
    public String description;
    public String requirements;
    public String location;
    public String category;
    public String type;
    public Integer salaryMin;
    public Integer salaryMax;
    public Boolean isClosed;
    public Boolean isSaved;
    public Integer applicationStatus;
    public LocalDateTime createdAt;
    public LocalDateTime updatedAt;
}
