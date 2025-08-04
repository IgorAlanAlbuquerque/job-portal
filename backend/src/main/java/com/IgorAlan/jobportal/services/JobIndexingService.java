package com.IgorAlan.jobportal.services;

import org.springframework.stereotype.Service;

import com.IgorAlan.jobportal.elasticsearch.document.JobPostDocument;
import com.IgorAlan.jobportal.elasticsearch.repository.JobSearchRepository;
import com.IgorAlan.jobportal.mapper.JobPostMapper;
import com.IgorAlan.jobportal.models.JobPostActivity;

@Service
public class JobIndexingService {

    private final JobSearchRepository jobSearchRepository;
    private final JobPostMapper jobPostMapper;

    public JobIndexingService(JobSearchRepository jobSearchRepository, JobPostMapper jobPostMapper) {
        this.jobSearchRepository = jobSearchRepository;
        this.jobPostMapper = jobPostMapper;
    }

    public void indexJobPost(JobPostActivity jobPostActivity) {
        JobPostDocument document = jobPostMapper.toDocument(jobPostActivity);
        jobSearchRepository.save(document);
    }
}
