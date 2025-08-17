package com.igoralan.jobportal.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.igoralan.jobportal.models.Job;
import com.igoralan.jobportal.models.JobSeekerApply;
import com.igoralan.jobportal.models.JobSeekerProfile;

import java.util.List;

@Repository
public interface JobSeekerApplyRepository extends JpaRepository<JobSeekerApply, Long> {

    boolean existsByProfileAndJob(JobSeekerProfile profile, Job job);

    List<JobSeekerApply> findByProfile(JobSeekerProfile profile);

    List<JobSeekerApply> findByJob_JobPostId(Long jobId);
}
