package com.IgorAlan.jobportal.controller;

import com.IgorAlan.jobportal.models.JobSeekerProfile;
import com.IgorAlan.jobportal.models.Skills;
import com.IgorAlan.jobportal.models.User;
import com.IgorAlan.jobportal.repository.UsersRepository;
import com.IgorAlan.jobportal.services.JobSeekerProfileService;
import com.IgorAlan.jobportal.util.FileDownloadUtil;
import com.IgorAlan.jobportal.util.FileUploadUtil;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/job-seeker-profile")
public class JobSeekerProfileController {

    private final JobSeekerProfileService jobSeekerProfileService;

    private final UsersRepository usersRepository;

    public JobSeekerProfileController(JobSeekerProfileService jobSeekerProfileService, UsersRepository usersRepository) {
        this.jobSeekerProfileService = jobSeekerProfileService;
        this.usersRepository = usersRepository;
    }

    @GetMapping("/")
    public ResponseEntity<?> jobSeekerProfile() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication instanceof AnonymousAuthenticationToken) {
            return ResponseEntity.status(401).body("Unauthorized");
        }

        User user = usersRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new UsernameNotFoundException("User not found."));
        Optional<JobSeekerProfile> seekerProfile = jobSeekerProfileService.getOne(user.getUserId());

        if (seekerProfile.isPresent()) {
            JobSeekerProfile jobSeekerProfile = seekerProfile.get();
            if (jobSeekerProfile.getSkills().isEmpty()) {
                jobSeekerProfile.setSkills(new ArrayList<>());
            }
            return ResponseEntity.ok(jobSeekerProfile);  // Return profile details as JSON
        }

        return ResponseEntity.status(404).body("Profile not found");
    }

    @PostMapping("/addNew")
    public ResponseEntity<?> addNew(@RequestBody JobSeekerProfile jobSeekerProfile,
                                    @RequestParam MultipartFile image,
                                    @RequestParam MultipartFile pdf) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication instanceof AnonymousAuthenticationToken) {
            return ResponseEntity.status(401).body("Unauthorized");
        }

        User user = usersRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new UsernameNotFoundException("User not found."));
        jobSeekerProfile.setUserId(user);
        jobSeekerProfile.setUserAccountId(user.getUserId());

        List<Skills> skillsList = jobSeekerProfile.getSkills();
        for (Skills skill : skillsList) {
            skill.setJobSeekerProfile(jobSeekerProfile);
        }

        String imageName = "", resumeName = "";

        if (!image.isEmpty()) {
            imageName = StringUtils.cleanPath(image.getOriginalFilename());
            jobSeekerProfile.setProfilePhoto(imageName);
        }

        if (!pdf.isEmpty()) {
            resumeName = StringUtils.cleanPath(pdf.getOriginalFilename());
            jobSeekerProfile.setResume(resumeName);
        }

        jobSeekerProfileService.addNew(jobSeekerProfile);

        try {
            String uploadDir = "photos/candidate/" + jobSeekerProfile.getUserAccountId();
            if (!image.isEmpty()) {
                FileUploadUtil.saveFile(uploadDir, imageName, image);
            }
            if (!pdf.isEmpty()) {
                FileUploadUtil.saveFile(uploadDir, resumeName, pdf);
            }
        } catch (IOException ex) {
            return ResponseEntity.status(500).body("Error during file upload: " + ex.getMessage());
        }

        return ResponseEntity.status(201).body("Profile created successfully");
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> candidateProfile(@PathVariable Long id) {
        Optional<JobSeekerProfile> seekerProfile = jobSeekerProfileService.getOne(id);
        if (seekerProfile.isPresent()) {
            return ResponseEntity.ok(seekerProfile.get());
        }
        return ResponseEntity.status(404).body("Profile not found");
    }

    @GetMapping("/downloadResume")
    public ResponseEntity<?> downloadResume(@RequestParam String fileName, @RequestParam("userID") String userId) {
        FileDownloadUtil downloadUtil = new FileDownloadUtil();
        Resource resource;

        try {
            resource = downloadUtil.getFileAsResource("photos/candidate/" + userId, fileName);
        } catch (IOException e) {
            return ResponseEntity.status(400).body("Error during file download");
        }

        if (resource == null) {
            return ResponseEntity.status(404).body("File not found");
        }

        String contentType = "application/octet-stream";
        String headerValue = "attachment; filename=\"" + resource.getFilename() + "\"";

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, headerValue)
                .body(resource);
    }
}