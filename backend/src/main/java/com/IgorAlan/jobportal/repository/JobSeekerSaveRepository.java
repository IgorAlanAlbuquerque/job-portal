package com.IgorAlan.jobportal.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.IgorAlan.jobportal.models.Job;
import com.IgorAlan.jobportal.models.JobSeekerProfile;
import com.IgorAlan.jobportal.models.JobSeekerSave;

import java.util.List;

@Repository
public interface JobSeekerSaveRepository extends JpaRepository<JobSeekerSave, Long> {

    List<JobSeekerSave> findByProfile(JobSeekerProfile profile);

    boolean existsByProfileAndJob(JobSeekerProfile profile, Job job);

}
