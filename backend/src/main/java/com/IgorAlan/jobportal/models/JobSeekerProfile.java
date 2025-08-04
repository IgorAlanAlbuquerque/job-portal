package com.IgorAlan.jobportal.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = {"user", "skills"})
@Entity
@Table(name = "job_seeker_profile")
public class JobSeekerProfile {

    @Id
    private Long userAccountId;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "user_account_id")
    private User user;

    private String workAuthorization;
    private String employmentType;
    private String resume;

    @Column(length = 255)
    private String profilePhoto;

    @OneToMany(mappedBy = "jobSeekerProfile", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Skill> skills = new ArrayList<>();

    public JobSeekerProfile(User user) {
        this.user = user;
    }
}
