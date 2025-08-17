package com.igoralan.jobportal.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.igoralan.jobportal.models.Job;
import com.igoralan.jobportal.models.dtos.RecruiterJobsDto;

import java.util.List;

public interface JobRepository extends JpaRepository<Job, Long> {

    @Query("SELECT new com.igoralan.jobportal.models.dtos.RecruiterJobsDto(" +
            "  COUNT(s.id), " +
            "  j.jobPostId, " +
            "  j.jobTitle, " +
            "  j.jobLocation, " +
            "  j.jobCompany) " +
            "FROM Job j LEFT JOIN JobSeekerApply s ON s.job = j " +
            "WHERE j.postedBy.userId = :recruiterId " +
            "GROUP BY j.jobPostId, j.jobTitle, j.jobLocation, j.jobCompany")
    List<RecruiterJobsDto> getRecruiterJobs(@Param("recruiterId") Long recruiterId);
}
