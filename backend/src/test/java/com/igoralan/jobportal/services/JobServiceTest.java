package com.igoralan.jobportal.services;

import com.igoralan.jobportal.elasticsearch.document.JobDocument;
import com.igoralan.jobportal.exception.AccessDeniedException;
import com.igoralan.jobportal.mapper.JobMapper;
import com.igoralan.jobportal.models.*;
import com.igoralan.jobportal.models.dtos.CreateJobDto;
import com.igoralan.jobportal.models.dtos.JobDetailDto;
import com.igoralan.jobportal.models.dtos.JobSummaryDto;
import com.igoralan.jobportal.models.dtos.RecruiterJobsDto;
import com.igoralan.jobportal.models.dtos.UpdateJobDto;
import com.igoralan.jobportal.repository.JobRepository;
import com.igoralan.jobportal.repository.JobSeekerApplyRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.data.elasticsearch.ResourceNotFoundException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JobServiceTest {

    @Mock
    private JobRepository jobRepository;
    @Mock
    private UserService userService;
    @Mock
    private JobMapper jobMapper;
    @Mock
    private RabbitTemplate rabbitTemplate;
    @Mock
    private JobSeekerService jobSeekerService;
    @Mock
    private LocationService locationService;
    @Mock
    private CompanyService companyService;
    @Mock
    private JobSeekerApplyRepository jobSeekerApplyRepository;

    @Mock
    private com.igoralan.jobportal.elasticsearch.repository.JobSearchRepository jobSearchRepository;

    @InjectMocks
    private JobService jobService;

    private User mockUser;
    private Job mockJob;
    private final Long jobId = 1L;
    private final Long userId = 10L;

    @BeforeEach
    void setUp() {
        mockUser = new User();
        mockUser.setUserId(userId);

        mockJob = new Job();
        mockJob.setJobPostId(jobId);
        mockJob.setPostedBy(mockUser);
    }

    @Test
    void createNewJob_shouldSaveAndSendMessage() {
        CreateJobDto createDto = new CreateJobDto(
                "Engenheiro de Software",
                "Descrição da vaga de engenharia.",
                1L,
                1L,
                "Tempo Integral",
                "A combinar",
                "Híbrido");

        JobLocation mockLocation = new JobLocation();
        JobCompany mockCompany = new JobCompany();

        JobDetailDto expectedDto = new JobDetailDto(
                1L,
                "Engenheiro de Software",
                "Descrição...",
                "Tech Corp",
                null,
                "São Paulo",
                "SP",
                "Brasil",
                "Tempo Integral",
                "A combinar",
                "Híbrido",
                LocalDateTime.now(),
                null,
                false,
                false);

        when(locationService.findById(anyLong())).thenReturn(mockLocation);
        when(companyService.findById(anyLong())).thenReturn(mockCompany);
        when(jobMapper.toEntity(createDto)).thenReturn(mockJob);
        when(jobRepository.save(any(Job.class))).thenReturn(mockJob);
        when(jobMapper.toDetailDto(any(Job.class), anyBoolean(), anyBoolean())).thenReturn(expectedDto);
        when(userService.getCurrentAuthenticatedUser()).thenReturn(mockUser);

        jobService.createNewJob(createDto);

        verify(jobRepository).save(any(Job.class));
        verify(rabbitTemplate).convertAndSend("job-exchange", "job.created", jobId);
    }

    @Test
    void getJobDetails_shouldReturnCorrectDtoWithUserStatus() {
        when(jobRepository.findById(jobId)).thenReturn(Optional.of(mockJob));
        when(jobSeekerService.getSavedJobIdsForCurrentUser()).thenReturn(Set.of(1L));
        when(jobSeekerService.getAppliedJobIdsForCurrentUser()).thenReturn(Set.of(2L));

        jobService.getJobDetails(jobId);

        verify(jobMapper).toDetailDto(mockJob, true, false);
    }

    @Test
    void updateJob_shouldUpdateAndSendMessage_whenUserIsOwner() {
        UpdateJobDto updateDto = new UpdateJobDto(
                "Engenheiro de Software Sênior",
                "Nova descrição da vaga.",
                2L,
                3L,
                "Tempo Integral",
                "R$ 15.000",
                "Remoto");
        when(jobRepository.findById(jobId)).thenReturn(Optional.of(mockJob));
        when(jobRepository.save(any(Job.class))).thenReturn(mockJob);
        when(userService.getCurrentAuthenticatedUser()).thenReturn(mockUser);

        jobService.updateJob(jobId, updateDto);

        verify(jobMapper).updateEntityFromDto(updateDto, mockJob);
        verify(jobRepository).save(mockJob);
        verify(rabbitTemplate).convertAndSend(eq("job-exchange"), eq("job.updated"), any(Long.class));
    }

    @Test
    void updateJob_shouldThrowAccessDeniedException_whenUserIsNotOwner() {
        User anotherUser = new User();
        anotherUser.setUserId(99L);
        mockJob.setPostedBy(anotherUser);

        when(jobRepository.findById(jobId)).thenReturn(Optional.of(mockJob));
        when(userService.getCurrentAuthenticatedUser()).thenReturn(mockUser);

        assertThrows(AccessDeniedException.class, () -> {
            jobService.updateJob(jobId, new UpdateJobDto(null, null, null, null, null, null, null));
        });

        verify(jobRepository, never()).save(any());
        verify(rabbitTemplate, never()).convertAndSend(anyString(), anyString(), any(Long.class));
    }

    @Test
    void deleteJob_shouldDeleteAndSendMessage_whenUserIsOwner() {
        when(jobRepository.findById(jobId)).thenReturn(Optional.of(mockJob));
        when(userService.getCurrentAuthenticatedUser()).thenReturn(mockUser);

        jobService.deleteJob(jobId);

        verify(jobRepository).delete(mockJob);
        verify(rabbitTemplate).convertAndSend("job-exchange", "job.deleted", jobId);
    }

    @Test
    void deleteJob_shouldThrowAccessDeniedException_whenUserIsNotOwner() {
        User anotherUser = new User();
        anotherUser.setUserId(99L);

        mockJob.setPostedBy(anotherUser);
        when(jobRepository.findById(jobId)).thenReturn(Optional.of(mockJob));
        when(userService.getCurrentAuthenticatedUser()).thenReturn(mockUser);

        assertThrows(AccessDeniedException.class, () -> jobService.deleteJob(jobId));
        verify(jobRepository, never()).delete(any());
    }

    @Test
    void getJobApplicants_shouldThrowResourceNotFoundException_whenJobNotFound() {
        when(jobRepository.findById(jobId)).thenReturn(Optional.empty());
        when(userService.getCurrentAuthenticatedUser()).thenReturn(mockUser);

        assertThrows(ResourceNotFoundException.class, () -> {
            jobService.getJobApplicants(jobId);
        });
    }

    @Test
    void getJobApplicants_shouldReturnApplicants_whenUserIsOwner() {
        when(jobRepository.findById(jobId)).thenReturn(Optional.of(mockJob));
        when(userService.getCurrentAuthenticatedUser()).thenReturn(mockUser);

        List<JobSeekerApply> applications = List.of(new JobSeekerApply(), new JobSeekerApply());
        when(jobSeekerApplyRepository.findByJob_JobPostId(jobId)).thenReturn(applications);

        jobService.getJobApplicants(jobId);

        verify(jobMapper, times(applications.size())).toApplicantSummaryDto(any(JobSeekerApply.class));
    }

    @Test
    void getJobApplicants_shouldThrowAccessDeniedException_whenUserIsNotOwner() {
        User anotherUser = new User();
        anotherUser.setUserId(99L);
        mockJob.setPostedBy(anotherUser);

        when(jobRepository.findById(jobId)).thenReturn(Optional.of(mockJob));
        when(userService.getCurrentAuthenticatedUser()).thenReturn(mockUser);

        assertThrows(AccessDeniedException.class, () -> {
            jobService.getJobApplicants(jobId);
        });
    }

    @Test
    void getRecruiterJobs_shouldReturnJobsForCurrentUser() {
        when(userService.getCurrentAuthenticatedUser()).thenReturn(mockUser);

        JobLocation location = new JobLocation();
        JobCompany company = new JobCompany();

        RecruiterJobsDto dto1 = new RecruiterJobsDto(1L, 10L, "Vaga de Java", location, company);
        RecruiterJobsDto dto2 = new RecruiterJobsDto(2L, 15L, "Vaga de Python", location, company);
        List<RecruiterJobsDto> expectedJobs = List.of(dto1, dto2);

        when(jobRepository.getRecruiterJobs(mockUser.getUserId())).thenReturn(expectedJobs);

        List<RecruiterJobsDto> actualJobs = jobService.getRecruiterJobs();

        assertThat(actualJobs).isEqualTo(expectedJobs);
        verify(jobRepository).getRecruiterJobs(mockUser.getUserId());
    }

    @Test
    void getAll_shouldReturnAllJobs() {
        List<Job> expectedJobs = List.of(new Job(), new Job());
        when(jobRepository.findAll()).thenReturn(expectedJobs);

        List<Job> actualJobs = jobService.getAll();

        assertThat(actualJobs).hasSize(2);
        assertThat(actualJobs).isEqualTo(expectedJobs);
        verify(jobRepository).findAll();
    }

    @Test
    void searchJobs_shouldReturnPageOfSummaries() {
        String keyword = "Java";
        String location = "São Paulo";
        Pageable pageable = PageRequest.of(0, 10);

        JobDocument doc1 = new JobDocument();
        JobDocument doc2 = new JobDocument();
        List<JobDocument> documents = List.of(doc1, doc2);
        Page<JobDocument> resultsPage = new PageImpl<>(documents, pageable, documents.size());

        when(jobSearchRepository.findByJobTitleContainsOrDescriptionContains(keyword, location, pageable))
                .thenReturn(resultsPage);

        when(jobMapper.toSummaryDto(any(JobDocument.class)))
                .thenReturn(new JobSummaryDto(null, null, null, null, null, null));

        Page<JobSummaryDto> dtoPage = jobService.searchJobs(keyword, location, pageable);

        assertThat(dtoPage).isNotNull();
        assertThat(dtoPage.getContent()).hasSize(2);
        verify(jobMapper, times(2)).toSummaryDto(any(JobDocument.class));
    }

    @Test
    void getJobDetails_shouldThrowResourceNotFoundException_whenJobNotFound() {
        Long nonExistentJobId = 999L;

        when(jobRepository.findById(nonExistentJobId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            jobService.getJobDetails(nonExistentJobId);
        });

        verify(jobSeekerService, never()).getSavedJobIdsForCurrentUser();
        verify(jobSeekerService, never()).getAppliedJobIdsForCurrentUser();
        verify(jobMapper, never()).toDetailDto(any(), anyBoolean(), anyBoolean());
    }
}