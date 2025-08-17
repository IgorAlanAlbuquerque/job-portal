package com.igoralan.jobportal.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.igoralan.jobportal.models.dtos.ApplicantSummaryDto;
import com.igoralan.jobportal.models.dtos.CreateJobDto;
import com.igoralan.jobportal.models.dtos.JobDetailDto;
import com.igoralan.jobportal.models.dtos.JobSummaryDto;
import com.igoralan.jobportal.models.dtos.RecruiterJobsDto;
import com.igoralan.jobportal.models.dtos.UpdateJobDto;
import com.igoralan.jobportal.services.JobService;

import java.util.*;

@RestController
@RequestMapping("/api/jobs")
public class JobController {

    private final JobService jobService;

    public JobController(JobService jobService) {
        this.jobService = jobService;
    }

    @PreAuthorize("hasAuthority('Recruiter')")
    @GetMapping("/my-jobs")
    public ResponseEntity<List<RecruiterJobsDto>> getRecruiterJobs() {
        List<RecruiterJobsDto> recruiterJobs = jobService.getRecruiterJobs();
        return ResponseEntity.ok(recruiterJobs);
    }

    @PreAuthorize("hasAuthority('Recruiter')")
    @PostMapping("/")
    public ResponseEntity<JobDetailDto> createNewJob(@RequestBody CreateJobDto createJobDto) {
        JobDetailDto createdJob = jobService.createNewJob(createJobDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdJob);
    }

    @GetMapping("/{id}")
    public ResponseEntity<JobDetailDto> getJobDetails(@PathVariable Long id) {
        JobDetailDto jobDetails = jobService.getJobDetails(id);
        return ResponseEntity.ok(jobDetails);
    }

    @GetMapping("/search")
    public ResponseEntity<Page<JobSummaryDto>> searchJobs(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String location,
            Pageable pageable) {

        Page<JobSummaryDto> results = jobService.searchJobs(keyword, location, pageable);
        return ResponseEntity.ok(results);
    }

    @GetMapping("/{id}/applicants")
    @PreAuthorize("hasAuthority('Recruiter')")
    public ResponseEntity<List<ApplicantSummaryDto>> getJobApplicants(@PathVariable Long id) {
        List<ApplicantSummaryDto> applicants = jobService.getJobApplicants(id);
        return ResponseEntity.ok(applicants);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('Recruiter')")
    public ResponseEntity<Void> deleteJob(@PathVariable Long id) {
        jobService.deleteJob(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('Recruiter')")
    public ResponseEntity<JobDetailDto> updateJob(@PathVariable Long id, @RequestBody UpdateJobDto updateDto) {
        JobDetailDto updatedJob = jobService.updateJob(id, updateDto);
        return ResponseEntity.ok(updatedJob);
    }
}
