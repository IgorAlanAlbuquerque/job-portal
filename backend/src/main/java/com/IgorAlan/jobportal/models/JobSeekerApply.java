package com.IgorAlan.jobportal.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Entity
@Table(name = "job_seeker_apply", uniqueConstraints = {
        @UniqueConstraint(columnNames = { "profile_id", "job_id" })
})
public class JobSeekerApply {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "profile_id")
    private JobSeekerProfile profile;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "job_id")
    private Job job;

    @Column(updatable = false)
    private LocalDateTime applyDate;

    private String coverLetter;

    public JobSeekerApply(JobSeekerProfile profile, Job job) {
        this.profile = profile;
        this.job = job;
    }

    @PrePersist
    protected void onCreate() {
        this.applyDate = LocalDateTime.now();
    }
}
