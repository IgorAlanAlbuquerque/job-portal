package com.IgorAlan.jobportal.controller;

import com.IgorAlan.jobportal.models.dtos.JobSummaryDto;
import com.IgorAlan.jobportal.services.JobSeekerService;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/job-seeker")
public class JobSeekerController {

    private final JobSeekerService jobSeekerService;

    public JobSeekerController(JobSeekerService jobSeekerService) {
        this.jobSeekerService = jobSeekerService;
    }

    @PostMapping("/apply/{jobId}")
    @PreAuthorize("hasAuthority('JobSeeker')")
    public ResponseEntity<Void> applyToJob(@PathVariable Long jobId) {
        jobSeekerService.applyToJob(jobId);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping("/save/{jobId}")
    @PreAuthorize("hasAuthority('JobSeeker')")
    public ResponseEntity<Void> saveJob(@PathVariable Long jobId) {
        jobSeekerService.saveJob(jobId);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("/saved-jobs")
    @PreAuthorize("hasAuthority('JobSeeker')")
    public ResponseEntity<List<JobSummaryDto>> getSavedJobs() {
        List<JobSummaryDto> savedJobs = jobSeekerService.getSavedJobsForCurrentUser();
        return ResponseEntity.ok(savedJobs);
    }
}