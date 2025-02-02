package com.IgorAlan.jobportal.services;


import com.IgorAlan.jobportal.entity.*;
import com.IgorAlan.jobportal.repository.JobPostActivityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class JobPostActivityService {

    private final JobPostActivityRepository jobPostActivityRepository;

    @Autowired
    public JobPostActivityService(JobPostActivityRepository jobPostActivityRepository) {
        this.jobPostActivityRepository = jobPostActivityRepository;
    }

    public JobPostActivity addNew(JobPostActivity jobPostActivity) {
        return jobPostActivityRepository.save(jobPostActivity);
    }

    public List<RecruiterJobsDto> getRecruiterJobs(int recruiter){
        List<IRecruiterJobs> recruiterJobsDtos = jobPostActivityRepository.getRecruiterJobs(recruiter);

        List<RecruiterJobsDto> recruiterJobsDtosList = new ArrayList<>();

        for(IRecruiterJobs rec : recruiterJobsDtos){
            JobLocation loc = new JobLocation(rec.getState(), rec.getLocationId(), rec.getCity(), rec.getCountry());
            JobCompany company = new JobCompany("", rec.getName(), rec.getCompanyId());
            recruiterJobsDtosList.add(new RecruiterJobsDto(company, loc, rec.getJob_title(), rec.getJob_post_id(), rec.getTotalCandidates()));
        }
        return recruiterJobsDtosList;
    }

    public JobPostActivity getOne(int id) {
        return jobPostActivityRepository.findById(id).orElseThrow(() -> new RuntimeException("Job Post Activity Not Found"));
    }
}
