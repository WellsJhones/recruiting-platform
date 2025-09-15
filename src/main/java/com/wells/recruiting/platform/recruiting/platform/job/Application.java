package com.wells.recruiting.platform.recruiting.platform.job;

import com.wells.recruiting.platform.recruiting.platform.model.Status;
import com.wells.recruiting.platform.recruiting.platform.user.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
@Getter
@Setter
@Entity(name = "application")
@Table(name = "application")
public class Application {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long _id;

    @ManyToOne
    @JoinColumn(name = "job_id")
    private Job job;

    @ManyToOne
    @JoinColumn(name = "applicant_id")
    private User applicant;
    // Possible values: "APPLIED", "IN_REVIEW", "REJECTED", "ACCEPTED"
    @Enumerated(EnumType.STRING)
    private Status status;


    private Instant createdAt;
    private Instant updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = updatedAt = Instant.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = Instant.now();
    }


    public Object getResume() {
        return applicant.getResume();
    }
}
