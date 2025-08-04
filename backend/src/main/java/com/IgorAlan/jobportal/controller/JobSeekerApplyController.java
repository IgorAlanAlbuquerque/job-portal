package com.IgorAlan.jobportal.controller;


import com.IgorAlan.jobportal.models.*;
import com.IgorAlan.jobportal.services.*;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/job-seeker-apply")
public class JobSeekerApplyController {

    private final JobPostActivityService jobPostActivityService;
    private final UsersService usersService;
    private final JobSeekerApplyService jobSeekerApplyService;
    private final JobSeekerSaveService jobSeekerSaveService;
    private final RecruiterProfileService recruiterProfileService;
    private final JobSeekerProfileService jobSeekerProfileService;


    public JobSeekerApplyController(JobPostActivityService jobPostActivityService, UsersService usersService, JobSeekerApplyService jobSeekerApplyService, JobSeekerSaveService jobSeekerSaveService, RecruiterProfileService recruiterProfileService, JobSeekerProfileService jobSeekerProfileService) {
        this.jobPostActivityService = jobPostActivityService;
        this.usersService = usersService;
        this.jobSeekerApplyService = jobSeekerApplyService;
        this.jobSeekerSaveService = jobSeekerSaveService;
        this.recruiterProfileService = recruiterProfileService;
        this.jobSeekerProfileService = jobSeekerProfileService;
    }

    @GetMapping("job-details-apply/{id}")
    public ResponseEntity<?> display(@PathVariable Long id) {
        JobPostActivity jobDetails = jobPostActivityService.getOne(id);
        if (jobDetails == null) {
            return ResponseEntity.status(404).body("Job post not found");
        }

        List<JobSeekerApply> jobSeekerApplyList = jobSeekerApplyService.getJobCandidates(jobDetails);
        List<JobSeekerSave> jobSeekerSaveList = jobSeekerSaveService.getJobCandidates(jobDetails);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication instanceof AnonymousAuthenticationToken) {
            return ResponseEntity.status(401).body("Unauthorized");
        }

        if (authentication.getAuthorities().contains(new SimpleGrantedAuthority("Recruiter"))) {
            RecruiterProfile recruiter = recruiterProfileService.getCurrentRecruiterProfile();
            if (recruiter != null) {
                return ResponseEntity.ok(Collections.singletonMap("applyList", jobSeekerApplyList));
            }
        } else {
            JobSeekerProfile jobSeekerProfile = jobSeekerProfileService.getCurrentSeekerProfile();
            if (jobSeekerProfile != null) {
                boolean exists = jobSeekerApplyList.stream()
                        .anyMatch(apply -> apply.getUserId().getUserAccountId() == jobSeekerProfile.getUserAccountId());
                boolean saved = jobSeekerSaveList.stream()
                        .anyMatch(save -> save.getUserId().getUserAccountId() == jobSeekerProfile.getUserAccountId());

                Map<String, Boolean> response = new HashMap<>();
                response.put("alreadyApplied", exists);
                response.put("alreadySaved", saved);
                return ResponseEntity.ok(response);
            }
        }

        return ResponseEntity.status(400).body("Invalid request");
    }

    @PostMapping("job-details/apply/{id}")
    public ResponseEntity<?> apply(@PathVariable Long id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication instanceof AnonymousAuthenticationToken) {
            return ResponseEntity.status(401).body("Unauthorized");
        }

        String currentUsername = authentication.getName();
        User user = usersService.findByEmail(currentUsername);
        if (user == null) {
            return ResponseEntity.status(404).body("User not found");
        }

        Optional<JobSeekerProfile> seekerProfile = jobSeekerProfileService.getOne(user.getUserId());
        JobPostActivity jobPostActivity = jobPostActivityService.getOne(id);

        if (seekerProfile.isPresent() && jobPostActivity != null) {
            JobSeekerApply jobSeekerApply = new JobSeekerApply();
            jobSeekerApply.setUserId(seekerProfile.get());
            jobSeekerApply.setJob(jobPostActivity);
            jobSeekerApply.setApplyDate(new Date());

            jobSeekerApplyService.addNew(jobSeekerApply);

            return ResponseEntity.status(201).body("Application submitted successfully");
        }

        return ResponseEntity.status(400).body("Error applying for the job");
    }
}
