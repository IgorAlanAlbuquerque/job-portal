package com.IgorAlan.jobportal.models;


import jakarta.persistence.*;

import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Entity
@Table(name = "job")
public class Job {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long jobPostId;

    @Column(nullable = false)
    private String jobTitle;

    @Column(columnDefinition = "TEXT")
    private String descriptionOfJob;

    private boolean active;

    private String jobType;
    private String salary;
    private String remote;

    @Column(updatable = false)
    private LocalDateTime postedDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "posted_by_id")
    private User postedBy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "job_location_id")
    private JobLocation jobLocation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "job_company_id")
    private JobCompany jobCompany;

    @PrePersist
    protected void onCreate() {
        this.postedDate = LocalDateTime.now();
    }
}
