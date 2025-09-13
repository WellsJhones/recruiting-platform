// src/main/java/com/wells/recruiting/platform/recruiting/platform/job/Job.java
package com.wells.recruiting.platform.recruiting.platform.job;

import jakarta.persistence.*;
import com.wells.recruiting.platform.recruiting.platform.company.Employer;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Table(name = "job")
public class Job {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "_id")
    private Long _id;

    private String title;
    private String description;
    private String requirements;
    private String location;
    private String category;
    private String type;
    private Integer salaryMin;
    private Integer salaryMax;
    private Boolean isClosed = false;
    private Boolean isSaved = false;
    private String applicationStatus = null;
    private LocalDateTime createdAt = LocalDateTime.now();
    private LocalDateTime updatedAt = LocalDateTime.now();

    public boolean isClosed() {
        return Boolean.TRUE.equals(isClosed);
    }



    @ManyToOne
    @JoinColumn(name = "employer_id")
    private Employer employer;

}

