package com.igoralan.jobportal.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.igoralan.jobportal.models.Job;
import com.igoralan.jobportal.models.JobSeekerProfile;
import com.igoralan.jobportal.models.JobSeekerSave;

import java.util.List;

@Repository
public interface JobSeekerSaveRepository extends JpaRepository<JobSeekerSave, Long> {

    List<JobSeekerSave> findByProfile(JobSeekerProfile profile);

    boolean existsByProfileAndJob(JobSeekerProfile profile, Job job);

}
