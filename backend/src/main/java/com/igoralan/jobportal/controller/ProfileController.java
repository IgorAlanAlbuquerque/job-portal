package com.igoralan.jobportal.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.igoralan.jobportal.models.dtos.UpdateJobSeekerProfileDto;
import com.igoralan.jobportal.models.dtos.UpdateRecruiterProfileDto;
import com.igoralan.jobportal.models.dtos.UserProfileDto;
import com.igoralan.jobportal.services.ProfileService;
import com.igoralan.jobportal.services.StorageService;

import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/profiles")
public class ProfileController {

    private final ProfileService profileService;
    private final StorageService storageService;

    public ProfileController(ProfileService profileService, StorageService storageService) {
        this.profileService = profileService;
        this.storageService = storageService;
    }

    @GetMapping("/me")
    public ResponseEntity<UserProfileDto> getCurrentUserProfile() {
        UserProfileDto userProfile = profileService.getCurrentUserProfile();
        return ResponseEntity.ok(userProfile);
    }

    @PutMapping("/recruiter")
    @PreAuthorize("hasAuthority('Recruiter')")
    public ResponseEntity<UserProfileDto> updateRecruiterProfile(@Valid @RequestBody UpdateRecruiterProfileDto dto) {
        UserProfileDto updatedProfile = profileService.updateRecruiterProfile(dto);
        return ResponseEntity.ok(updatedProfile);
    }

    @PutMapping("/job-seeker")
    @PreAuthorize("hasAuthority('JobSeeker')")
    public ResponseEntity<UserProfileDto> updateJobSeekerProfile(@Valid @RequestBody UpdateJobSeekerProfileDto dto) {
        UserProfileDto updatedProfile = profileService.updateJobSeekerProfile(dto);
        return ResponseEntity.ok(updatedProfile);
    }

    @PostMapping("/me/photo")
    public ResponseEntity<Void> uploadProfilePhoto(@RequestParam("image") MultipartFile multipartFile) {
        storageService.saveProfilePhoto(multipartFile);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/job-seeker/resume")
    @PreAuthorize("hasAuthority('JobSeeker')")
    public ResponseEntity<Void> uploadResume(@RequestParam("resume") MultipartFile multipartFile) {
        storageService.saveResume(multipartFile);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{userId}/resume")
    @PreAuthorize("hasAuthority('Recruiter')")
    public ResponseEntity<Resource> downloadResume(@PathVariable Long userId) {
        Resource resource = storageService.loadResume(userId);
        String contentType = "application/octet-stream";
        String headerValue = "attachment; filename=\"" + resource.getFilename() + "\"";

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, headerValue)
                .body(resource);
    }
}
