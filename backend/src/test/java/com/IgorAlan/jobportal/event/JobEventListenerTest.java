package com.IgorAlan.jobportal.event;

import com.IgorAlan.jobportal.elasticsearch.services.JobIndexingService;
import com.IgorAlan.jobportal.models.Job;
import com.IgorAlan.jobportal.repository.JobRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JobEventListenerTest {

    @Mock
    private JobRepository jobRepository;

    @Mock
    private JobIndexingService jobIndexingService;

    @InjectMocks
    private JobEventListener jobEventListener;

    @Test
    void handleJobSyncEvent_shouldIndexJob_whenJobExists() {
        Long jobId = 1L;
        Job job = new Job();
        job.setJobPostId(jobId);

        when(jobRepository.findById(jobId)).thenReturn(Optional.of(job));

        jobEventListener.handleJobSyncEvent(jobId);

        verify(jobIndexingService, times(1)).indexJob(job);
    }

    @Test
    void handleJobSyncEvent_shouldDoNothing_whenJobDoesNotExist() {
        Long jobId = 1L;

        when(jobRepository.findById(jobId)).thenReturn(Optional.empty());

        jobEventListener.handleJobSyncEvent(jobId);

        verify(jobIndexingService, never()).indexJob(any(Job.class));
    }

    @Test
    void handleJobDeleteEvent_shouldCallDeleteFromIndex() {
        Long jobId = 1L;

        jobEventListener.handleJobDeleteEvent(jobId);

        verify(jobIndexingService, times(1)).deleteJobFromIndex(jobId);
    }
}