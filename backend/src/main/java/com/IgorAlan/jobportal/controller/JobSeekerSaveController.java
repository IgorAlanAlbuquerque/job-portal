package com.IgorAlan.jobportal.controller;

import com.IgorAlan.jobportal.entity.JobPostActivity;
import com.IgorAlan.jobportal.entity.JobSeekerProfile;
import com.IgorAlan.jobportal.entity.JobSeekerSave;
import com.IgorAlan.jobportal.entity.Users;
import com.IgorAlan.jobportal.services.JobPostActivityService;
import com.IgorAlan.jobportal.services.JobSeekerProfileService;
import com.IgorAlan.jobportal.services.JobSeekerSaveService;
import com.IgorAlan.jobportal.services.UsersService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/job-seeker")
public class JobSeekerSaveController {

    private final UsersService usersService;
    private final JobSeekerProfileService jobSeekerProfileService;
    private final JobPostActivityService jobPostActivityService;
    private final JobSeekerSaveService jobSeekerSaveService;

    public JobSeekerSaveController(UsersService usersService, JobSeekerProfileService jobSeekerProfileService, JobPostActivityService jobPostActivityService, JobSeekerSaveService jobSeekerSaveService) {
        this.usersService = usersService;
        this.jobSeekerProfileService = jobSeekerProfileService;
        this.jobPostActivityService = jobPostActivityService;
        this.jobSeekerSaveService = jobSeekerSaveService;
    }

    @PostMapping("job-details/save/{id}")
    public ResponseEntity<?> save(@PathVariable("id") int id, JobSeekerSave jobSeekerSave) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!(authentication instanceof AnonymousAuthenticationToken)) {
            String currentUsername = authentication.getName();
            Users user = usersService.findByEmail(currentUsername);
            Optional<JobSeekerProfile> seekerProfile = jobSeekerProfileService.getOne(user.getUserId());
            JobPostActivity jobPostActivity = jobPostActivityService.getOne(id);
            if (seekerProfile.isPresent() && jobPostActivity != null) {
                jobSeekerSave.setJob(jobPostActivity);
                jobSeekerSave.setUserId(seekerProfile.get());
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

        Object currentUserProfile = usersService.getCurrentUserProfile();

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