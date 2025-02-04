package com.IgorAlan.jobportal.repository;

import com.IgorAlan.jobportal.entity.JobPostActivity;
import com.IgorAlan.jobportal.entity.JobSeekerProfile;
import com.IgorAlan.jobportal.entity.JobSeekerSave;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JobSeekerSaveRepository extends JpaRepository<JobSeekerSave, Integer> {

    List<JobSeekerSave> findByUserId(JobSeekerProfile userAccountId);

    List<JobSeekerSave> findByJob(JobPostActivity job);

}
