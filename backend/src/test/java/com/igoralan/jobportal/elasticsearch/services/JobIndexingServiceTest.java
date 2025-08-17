package com.igoralan.jobportal.elasticsearch.services;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.igoralan.jobportal.elasticsearch.document.JobDocument;
import com.igoralan.jobportal.elasticsearch.repository.JobSearchRepository;
import com.igoralan.jobportal.mapper.JobMapper;
import com.igoralan.jobportal.models.Job;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JobIndexingServiceTest {

    @Mock
    private JobSearchRepository jobSearchRepository;

    @Mock
    private JobMapper jobMapper;

    @InjectMocks
    private JobIndexingService jobIndexingService;

    @Test
    void indexJob_shouldCallMapperAndRepository_whenJobIsValid() {
        Job jobEntity = new Job();
        jobEntity.setJobPostId(1L);
        jobEntity.setJobTitle("Engenheiro de Software");

        JobDocument jobDocument = new JobDocument();
        jobDocument.setId(1L);
        jobDocument.setJobTitle("Engenheiro de Software");

        when(jobMapper.toDocument(any(Job.class))).thenReturn(jobDocument);

        jobIndexingService.indexJob(jobEntity);

        verify(jobMapper, times(1)).toDocument(jobEntity);

        verify(jobSearchRepository, times(1)).save(jobDocument);
    }

    @Test
    void deleteJobFromIndex_shouldCallRepositoryDeleteById() {
        Long jobIdToDelete = 42L;

        jobIndexingService.deleteJobFromIndex(jobIdToDelete);

        verify(jobSearchRepository, times(1)).deleteById(jobIdToDelete);
    }
}
