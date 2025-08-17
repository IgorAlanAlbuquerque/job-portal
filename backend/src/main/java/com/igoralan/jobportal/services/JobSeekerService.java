package com.igoralan.jobportal.services;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.igoralan.jobportal.exception.AlreadyExistsException;
import com.igoralan.jobportal.exception.ResourceNotFoundException;
import com.igoralan.jobportal.mapper.JobMapper;
import com.igoralan.jobportal.models.*;
import com.igoralan.jobportal.models.dtos.JobSummaryDto;
import com.igoralan.jobportal.repository.JobRepository;
import com.igoralan.jobportal.repository.JobSeekerApplyRepository;
import com.igoralan.jobportal.repository.JobSeekerSaveRepository;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class JobSeekerService {

    private final JobSeekerApplyRepository applyRepository;
    private final JobSeekerSaveRepository saveRepository;
    private final JobRepository jobRepository;
    private final ProfileService profileService;
    private final JobMapper jobMapper;

    public JobSeekerService(JobSeekerApplyRepository applyRepository,
            JobSeekerSaveRepository saveRepository,
            JobRepository jobRepository,
            ProfileService profileService,
            JobMapper jobMapper) {
        this.applyRepository = applyRepository;
        this.saveRepository = saveRepository;
        this.jobRepository = jobRepository;
        this.profileService = profileService;
        this.jobMapper = jobMapper;
    }

    @Transactional
    public void applyToJob(Long jobId) {
        JobSeekerProfile profile = profileService.getCurrentJobSeekerProfile();
        Job job = findJobById(jobId);

        if (applyRepository.existsByProfileAndJob(profile, job)) {
            throw new AlreadyExistsException("Você já se aplicou para esta vaga.");
        }

        JobSeekerApply application = new JobSeekerApply(profile, job);
        applyRepository.save(application);
    }

    @Transactional
    public void saveJob(Long jobId) {
        JobSeekerProfile profile = profileService.getCurrentJobSeekerProfile();
        Job job = findJobById(jobId);

        if (saveRepository.existsByProfileAndJob(profile, job)) {
            throw new AlreadyExistsException("Você já salvou esta vaga.");
        }

        JobSeekerSave savedJob = new JobSeekerSave(profile, job);
        saveRepository.save(savedJob);
    }

    public Set<Long> getAppliedJobIdsForCurrentUser() {
        JobSeekerProfile profile = profileService.getCurrentJobSeekerProfile();
        List<JobSeekerApply> applications = applyRepository.findByProfile(profile);
        return applications.stream()
                .map(application -> application.getJob().getJobPostId())
                .collect(Collectors.toSet());
    }

    public Set<Long> getSavedJobIdsForCurrentUser() {
        JobSeekerProfile profile = profileService.getCurrentJobSeekerProfile();
        List<JobSeekerSave> savedJobs = saveRepository.findByProfile(profile);
        return savedJobs.stream()
                .map(saved -> saved.getJob().getJobPostId())
                .collect(Collectors.toSet());
    }

    private Job findJobById(Long jobId) {
        return jobRepository.findById(jobId)
                .orElseThrow(() -> new ResourceNotFoundException("Vaga não encontrada com o ID: " + jobId));
    }

    public List<JobSummaryDto> getSavedJobsForCurrentUser() {
        JobSeekerProfile profile = profileService.getCurrentJobSeekerProfile();

        List<JobSeekerSave> savedJobs = saveRepository.findByProfile(profile);

        return savedJobs.stream()
                .map(JobSeekerSave::getJob)
                .map(jobMapper::toSummaryDto)
                .collect(Collectors.toList());
    }
}
