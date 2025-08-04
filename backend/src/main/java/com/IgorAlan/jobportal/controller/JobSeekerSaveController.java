package com.IgorAlan.jobportal.controller;

import com.IgorAlan.jobportal.models.JobPostActivity;
import com.IgorAlan.jobportal.models.JobSeekerProfile;
import com.IgorAlan.jobportal.models.JobSeekerSave;
import com.IgorAlan.jobportal.models.User;
import com.IgorAlan.jobportal.services.JobPostActivityService;
import com.IgorAlan.jobportal.services.JobSeekerProfileService;
import com.IgorAlan.jobportal.services.JobSeekerSaveService;
import com.IgorAlan.jobportal.services.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/job-seeker")
public class JobSeekerSaveController {

    private final UserService userService;
    private final JobSeekerProfileService jobSeekerProfileService;
    private final JobPostActivityService jobPostActivityService;
    private final JobSeekerSaveService jobSeekerSaveService;

    public JobSeekerSaveController(UserService userService, JobSeekerProfileService jobSeekerProfileService, JobPostActivityService jobPostActivityService, JobSeekerSaveService jobSeekerSaveService) {
        this.userService = userService;
        this.jobSeekerProfileService = jobSeekerProfileService;
        this.jobPostActivityService = jobPostActivityService;
        this.jobSeekerSaveService = jobSeekerSaveService;
    }

    @PostMapping("job-details/save/{id}")
    public ResponseEntity<?> save(@PathVariable Long id, JobSeekerSave jobSeekerSave) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!(authentication instanceof AnonymousAuthenticationToken)) {
            String currentUsername = authentication.getName();
            User user = userService.findByEmail(currentUsername);
            Optional<JobSeekerProfile> seekerProfile = jobSeekerProfileService.getOne(user.getUserId());
            JobPostActivity jobPostActivity = jobPostActivityService.getOne(id);
            if (seekerProfile.isPresent() && jobPostActivity != null) {
                jobSeekerSave.setJob(jobPostActivity);
                jobSeekerSave.setProfile(seekerProfile.get());
            } else {
                return ResponseEntity.status(404).body("User or Job Post not found");
            }
            jobSeekerSaveService.addNew(jobSeekerSave);
            return ResponseEntity.status(201).body("Job saved successfully");
        }
        return ResponseEntity.status(401).body("Unauthorized");
    }

    @GetMapping("saved-jobs/")
    public ResponseEntity<?> savedJobs() {

        Object currentUserProfile = userService.getCurrentUserProfile();

        if (currentUserProfile == null) {
            return ResponseEntity.status(404).body("User profile not found");
        }

        List<JobSeekerSave> jobSeekerSaveList = jobSeekerSaveService.getCandidatesJob((JobSeekerProfile) currentUserProfile);
        List<JobPostActivity> jobPostList = new ArrayList<>();

        for (JobSeekerSave jobSeekerSave : jobSeekerSaveList) {
            jobPostList.add(jobSeekerSave.getJob());
        }

        return ResponseEntity.ok(jobPostList);  // Return the list of saved jobs as JSON
    }
}