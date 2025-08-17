package com.igoralan.jobportal.elasticsearch.services;

import org.springframework.stereotype.Service;

import com.igoralan.jobportal.elasticsearch.document.JobDocument;
import com.igoralan.jobportal.elasticsearch.repository.JobSearchRepository;
import com.igoralan.jobportal.mapper.JobMapper;
import com.igoralan.jobportal.models.Job;

@Service
public class JobIndexingService {

    private final JobSearchRepository jobSearchRepository;
    private final JobMapper jobMapper;

    public JobIndexingService(JobSearchRepository jobSearchRepository, JobMapper jobMapper) {
        this.jobSearchRepository = jobSearchRepository;
        this.jobMapper = jobMapper;
    }

    public void indexJob(Job job) {
        JobDocument document = jobMapper.toDocument(job);
        jobSearchRepository.save(document);
    }

    public void deleteJobFromIndex(Long jobId) {
        jobSearchRepository.deleteById(jobId);
    }
}
