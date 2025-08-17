package com.igoralan.jobportal.event;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import com.igoralan.jobportal.elasticsearch.services.JobIndexingService;
import com.igoralan.jobportal.repository.JobRepository;

@Component
public class JobEventListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(JobEventListener.class);

    private final JobIndexingService jobIndexingService;
    private final JobRepository jobRepository;

    public JobEventListener(JobIndexingService indexer, JobRepository repo) {
        this.jobIndexingService = indexer;
        this.jobRepository = repo;
    }

    @RabbitListener(queues = "job.sync.queue")
    public void handleJobSyncEvent(Long jobId) {
        jobRepository.findById(jobId).ifPresent(job -> {
            jobIndexingService.indexJob(job);
            LOGGER.info("Job ID: {} has been indexed/updated.", jobId);
        });
    }

    @RabbitListener(queues = "job.delete.queue")
    public void handleJobDeleteEvent(Long jobId) {
        jobIndexingService.deleteJobFromIndex(jobId);
        LOGGER.info("Job ID: {} has been deleted from index.", jobId);
    }
}