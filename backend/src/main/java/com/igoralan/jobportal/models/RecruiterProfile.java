package com.igoralan.jobportal.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@ToString
@Table(name = "recruiter_profile")
public class RecruiterProfile {

    @Id
    private Long userAccountId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_account_id")
    @MapsId
    private User user;

    @Column(nullable = true, length = 64)
    private String profilePhoto;

    public RecruiterProfile(User user) {
        this.user = user;
    }
}
