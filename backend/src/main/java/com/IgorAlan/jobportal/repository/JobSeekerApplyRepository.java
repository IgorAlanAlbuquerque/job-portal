package com.IgorAlan.jobportal.repository;

import com.IgorAlan.jobportal.entity.JobPostActivity;
import com.IgorAlan.jobportal.entity.JobSeekerApply;
import com.IgorAlan.jobportal.entity.JobSeekerProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JobSeekerApplyRepository extends JpaRepository<JobSeekerApply, Integer> {

    List<JobSeekerApply> findByUserId(JobSeekerProfile userId);

    List<JobSeekerApply> findByJob(JobPostActivity job);
}
