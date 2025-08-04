package com.IgorAlan.jobportal.services;


import com.IgorAlan.jobportal.elasticsearch.document.JobDocument;
import com.IgorAlan.jobportal.elasticsearch.repository.JobSearchRepository;
import com.IgorAlan.jobportal.exception.AccessDeniedException;
import com.IgorAlan.jobportal.mapper.JobMapper;
import com.IgorAlan.jobportal.models.*;
import com.IgorAlan.jobportal.models.dtos.ApplicantSummaryDto;
import com.IgorAlan.jobportal.models.dtos.CreateJobDto;
import com.IgorAlan.jobportal.models.dtos.JobDetailDto;
import com.IgorAlan.jobportal.models.dtos.JobSummaryDto;
import com.IgorAlan.jobportal.models.dtos.RecruiterJobsDto;
import com.IgorAlan.jobportal.models.dtos.UpdateJobDto;
import com.IgorAlan.jobportal.repository.CompanyRepository;
import com.IgorAlan.jobportal.repository.JobRepository;
import com.IgorAlan.jobportal.repository.JobSeekerApplyRepository;
import com.IgorAlan.jobportal.repository.LocationRepository;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.ResourceNotFoundException;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class JobService {

    private final JobRepository jobRepository;
    private final UserService userService;
    private final JobSearchRepository jobSearchRepository;
    private final JobMapper jobMapper;
    private final RabbitTemplate rabbitTemplate;
    private final JobSeekerService jobSeekerService;
    private final LocationRepository locationRepository;
    private final CompanyRepository companyRepository;
    private final JobSeekerApplyRepository jobSeekerApplyRepository;

    public JobService(JobRepository jobRepository,
                                UserService userService,
                                JobSearchRepository jobSearchRepository,
                                JobMapper jobMapper,
                                RabbitTemplate rabbitTemplate,
                                JobSeekerService jobSeekerService,
                                LocationRepository locationRepository,
                                CompanyRepository companyRepository,
                                JobSeekerApplyRepository jobSeekerApplyRepository) {
        this.jobRepository = jobRepository;
        this.userService = userService;
        this.jobSearchRepository = jobSearchRepository;
        this.jobMapper = jobMapper;
        this.rabbitTemplate = rabbitTemplate;
        this.jobSeekerService = jobSeekerService;
        this.locationRepository = locationRepository;
        this.companyRepository = companyRepository;
        this.jobSeekerApplyRepository = jobSeekerApplyRepository;
    }

    @Transactional
    public JobDetailDto createNewJob(CreateJobDto createJobDto) {
        User currentUser = userService.getCurrentAuthenticatedUser();

        JobLocation location = locationRepository.findById(createJobDto.locationId())
                .orElseThrow(() -> new ResourceNotFoundException("Localização não encontrada com ID: " + createJobDto.locationId()));
        JobCompany company = companyRepository.findById(createJobDto.companyId())
                .orElseThrow(() -> new ResourceNotFoundException("Empresa não encontrada com ID: " + createJobDto.companyId()));

        Job newJob = jobMapper.toEntity(createJobDto);

        newJob.setPostedBy(currentUser);
        newJob.setJobLocation(location);
        newJob.setJobCompany(company);

        Job savedJob = jobRepository.save(newJob);

        rabbitTemplate.convertAndSend("job-exchange", "job.created", savedJob.getJobPostId());

        return jobMapper.toDetailDto(savedJob, false, false);
    }

    public List<RecruiterJobsDto> getRecruiterJobs() {
        User currentUser = userService.getCurrentAuthenticatedUser();
        return jobRepository.getRecruiterJobs(currentUser.getUserId());
    }

    public List<Job> getAll() {
        return jobRepository.findAll();
    }

    public Page<JobSummaryDto> searchJobs(String keyword, String location, Pageable pageable) {
        Page<JobDocument> resultsPage = jobSearchRepository
                .findByJobTitleContainsOrDescriptionContains(keyword, keyword, pageable);
        return resultsPage.map(jobMapper::toSummaryDto);
    }

    public JobDetailDto getJobDetails(Long id) {
        Job job = jobRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Vaga não encontrada com o ID: " + id));

        Set<Long> savedJobIds = jobSeekerService.getSavedJobIdsForCurrentUser();
        Set<Long> appliedJobIds = jobSeekerService.getAppliedJobIdsForCurrentUser();

        boolean isSaved = savedJobIds.contains(id);
        boolean isApplied = appliedJobIds.contains(id);

        return jobMapper.toDetailDto(job, isSaved, isApplied);
    }

    @Transactional
    public JobDetailDto updateJob(Long id, UpdateJobDto updateDto) {
        User currentUser = userService.getCurrentAuthenticatedUser();
        Job job = findJobByIdAndValidateOwnership(id, currentUser);

        

        jobMapper.updateEntityFromDto(updateDto, job);
        Job updatedJob = jobRepository.save(job);

        rabbitTemplate.convertAndSend("job-exchange", "job.updated", updatedJob.getJobPostId());

        return jobMapper.toDetailDto(updatedJob, false, false);
    }

    @Transactional
    public void deleteJob(Long id) {
        User currentUser = userService.getCurrentAuthenticatedUser();
        Job job = findJobByIdAndValidateOwnership(id, currentUser);

        jobRepository.delete(job);

        rabbitTemplate.convertAndSend("job-exchange", "job.deleted", id);
    }

    public List<ApplicantSummaryDto> getJobApplicants(Long id) {
        User currentUser = userService.getCurrentAuthenticatedUser();
        findJobByIdAndValidateOwnership(id, currentUser);

        List<JobSeekerApply> applications = jobSeekerApplyRepository.findByJob_JobPostId(id);

        return applications.stream()
                .map(jobMapper::toApplicantSummaryDto)
                .collect(Collectors.toList());
    }

    private Job findJobByIdAndValidateOwnership(Long jobId, User user) {
        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new ResourceNotFoundException("Vaga não encontrada com o ID: " + jobId));

        if (!job.getPostedBy().getUserId().equals(user.getUserId())) {
            throw new AccessDeniedException("Você não tem permissão para modificar esta vaga.");
        }
        return job;
    }
}
