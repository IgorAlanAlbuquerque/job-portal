package com.IgorAlan.jobportal.repository;

import com.IgorAlan.jobportal.entity.JobSeekerProfile;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JobSeekerProfileRepository extends JpaRepository<JobSeekerProfile, Integer> {
}
