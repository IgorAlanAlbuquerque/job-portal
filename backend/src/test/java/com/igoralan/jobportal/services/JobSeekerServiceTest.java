package com.igoralan.jobportal.services;

import com.igoralan.jobportal.exception.AlreadyExistsException;
import com.igoralan.jobportal.exception.ResourceNotFoundException;
import com.igoralan.jobportal.mapper.JobMapper;
import com.igoralan.jobportal.models.*;
import com.igoralan.jobportal.models.dtos.JobSummaryDto;
import com.igoralan.jobportal.repository.JobRepository;
import com.igoralan.jobportal.repository.JobSeekerApplyRepository;
import com.igoralan.jobportal.repository.JobSeekerSaveRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JobSeekerServiceTest {

    @Mock
    private JobSeekerApplyRepository applyRepository;
    @Mock
    private JobSeekerSaveRepository saveRepository;
    @Mock
    private JobRepository jobRepository;
    @Mock
    private ProfileService profileService;
    @Mock
    private JobMapper jobMapper;

    @InjectMocks
    private JobSeekerService jobSeekerService;

    private JobSeekerProfile mockProfile;
    private Job mockJob;
    private final Long jobId = 1L;

    @BeforeEach
    void setUp() {
        mockProfile = new JobSeekerProfile();
        mockJob = new Job();
        mockJob.setJobPostId(jobId);

        when(profileService.getCurrentJobSeekerProfile()).thenReturn(mockProfile);
    }

    @Test
    void applyToJob_shouldSaveApplication_whenJobExistsAndNotApplied() {
        when(jobRepository.findById(jobId)).thenReturn(Optional.of(mockJob));
        when(applyRepository.existsByProfileAndJob(mockProfile, mockJob)).thenReturn(false);

        jobSeekerService.applyToJob(jobId);

        verify(applyRepository).save(any(JobSeekerApply.class));
    }

    @Test
    void applyToJob_shouldThrowAlreadyExistsException_whenAlreadyApplied() {
        when(jobRepository.findById(jobId)).thenReturn(Optional.of(mockJob));
        when(applyRepository.existsByProfileAndJob(mockProfile, mockJob)).thenReturn(true);

        assertThrows(AlreadyExistsException.class, () -> jobSeekerService.applyToJob(jobId));
        verify(applyRepository, never()).save(any());
    }

    @Test
    void applyToJob_shouldThrowResourceNotFoundException_whenJobNotFound() {
        when(jobRepository.findById(jobId)).thenReturn(Optional.of(mockJob));
        when(jobRepository.findById(jobId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> jobSeekerService.applyToJob(jobId));
    }

    @Test
    void saveJob_shouldSaveJob_whenJobExistsAndNotSaved() {
        when(jobRepository.findById(jobId)).thenReturn(Optional.of(mockJob));
        when(saveRepository.existsByProfileAndJob(mockProfile, mockJob)).thenReturn(false);

        jobSeekerService.saveJob(jobId);

        verify(saveRepository).save(any(JobSeekerSave.class));
    }

    @Test
    void saveJob_shouldThrowAlreadyExistsException_whenAlreadySaved() {
        when(jobRepository.findById(jobId)).thenReturn(Optional.of(mockJob));
        when(saveRepository.existsByProfileAndJob(mockProfile, mockJob)).thenReturn(true);

        assertThrows(AlreadyExistsException.class, () -> jobSeekerService.saveJob(jobId));
        verify(saveRepository, never()).save(any());
    }

    @Test
    void getAppliedJobIdsForCurrentUser_shouldReturnSetOfJobIds() {
        Job job1 = new Job();
        job1.setJobPostId(1L);
        Job job2 = new Job();
        job2.setJobPostId(2L);
        List<JobSeekerApply> applications = List.of(new JobSeekerApply(mockProfile, job1),
                new JobSeekerApply(mockProfile, job2));
        when(applyRepository.findByProfile(mockProfile)).thenReturn(applications);

        Set<Long> appliedJobIds = jobSeekerService.getAppliedJobIdsForCurrentUser();

        assertThat(appliedJobIds).containsExactlyInAnyOrder(1L, 2L);
    }

    @Test
    void getSavedJobIdsForCurrentUser_shouldReturnSetOfJobIds() {
        Job job1 = new Job();
        job1.setJobPostId(3L);
        Job job2 = new Job();
        job2.setJobPostId(4L);
        List<JobSeekerSave> savedJobs = List.of(new JobSeekerSave(mockProfile, job1),
                new JobSeekerSave(mockProfile, job2));
        when(saveRepository.findByProfile(mockProfile)).thenReturn(savedJobs);

        Set<Long> savedJobIds = jobSeekerService.getSavedJobIdsForCurrentUser();

        assertThat(savedJobIds).containsExactlyInAnyOrder(3L, 4L);
    }

    @Test
    void getSavedJobsForCurrentUser_shouldReturnListOfJobSummaryDtos() {
        Job job1 = new Job();
        job1.setJobPostId(1L);
        Job job2 = new Job();
        job2.setJobPostId(2L);
        List<JobSeekerSave> savedJobs = List.of(new JobSeekerSave(mockProfile, job1),
                new JobSeekerSave(mockProfile, job2));
        JobSummaryDto dto1 = new JobSummaryDto(1L, "Engenheiro de Software", "Tech Corp", "São Paulo", "SP",
                LocalDateTime.now());
        JobSummaryDto dto2 = new JobSummaryDto(2L, "Analista de Dados", "Data Inc", "Rio de Janeiro", "RJ",
                LocalDateTime.now());

        when(saveRepository.findByProfile(mockProfile)).thenReturn(savedJobs);
        when(jobMapper.toSummaryDto(job1)).thenReturn(dto1);
        when(jobMapper.toSummaryDto(job2)).thenReturn(dto2);

        List<JobSummaryDto> resultDtos = jobSeekerService.getSavedJobsForCurrentUser();

        assertThat(resultDtos).hasSize(2);
        assertThat(resultDtos).containsExactly(dto1, dto2);
        verify(jobMapper, times(2)).toSummaryDto(any(Job.class));
    }
}