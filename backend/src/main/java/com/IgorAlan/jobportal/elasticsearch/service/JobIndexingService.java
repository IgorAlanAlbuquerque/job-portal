package com.IgorAlan.jobportal.elasticsearch.service;

import com.IgorAlan.jobportal.elasticsearch.document.JobDocument;
import com.IgorAlan.jobportal.elasticsearch.repository.JobSearchRepository;
import com.IgorAlan.jobportal.mapper.JobMapper;
import com.IgorAlan.jobportal.models.Job;
import org.springframework.stereotype.Service;

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
