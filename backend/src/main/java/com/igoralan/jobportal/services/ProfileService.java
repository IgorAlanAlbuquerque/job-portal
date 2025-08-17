package com.igoralan.jobportal.services;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.igoralan.jobportal.exception.ResourceNotFoundException;
import com.igoralan.jobportal.mapper.UserMapper;
import com.igoralan.jobportal.models.JobSeekerProfile;
import com.igoralan.jobportal.models.RecruiterProfile;
import com.igoralan.jobportal.models.User;
import com.igoralan.jobportal.models.dtos.UpdateJobSeekerProfileDto;
import com.igoralan.jobportal.models.dtos.UpdateRecruiterProfileDto;
import com.igoralan.jobportal.models.dtos.UserProfileDto;
import com.igoralan.jobportal.repository.JobSeekerProfileRepository;
import com.igoralan.jobportal.repository.RecruiterProfileRepository;

@Service
public class ProfileService {

    private final JobSeekerProfileRepository jobSeekerProfileRepository;
    private final RecruiterProfileRepository recruiterProfileRepository;
    private final UserService userService;
    private final UserMapper userMapper;

    public ProfileService(JobSeekerProfileRepository jobSeekerProfileRepository,
            RecruiterProfileRepository recruiterProfileRepository,
            UserService userService,
            UserMapper userMapper) {
        this.jobSeekerProfileRepository = jobSeekerProfileRepository;
        this.recruiterProfileRepository = recruiterProfileRepository;
        this.userService = userService;
        this.userMapper = userMapper;
    }

    @Transactional
    public void createProfileForNewUser(User user) {
        String userTypeName = user.getUserType().getUserTypeName();
        if ("Recruiter".equalsIgnoreCase(userTypeName)) {
            recruiterProfileRepository.save(new RecruiterProfile(user));
        } else {
            jobSeekerProfileRepository.save(new JobSeekerProfile(user));
        }
    }

    public UserProfileDto getCurrentUserProfile() {
        User currentUser = userService.getCurrentAuthenticatedUser();
        String userTypeName = currentUser.getUserType().getUserTypeName();

        if ("Recruiter".equalsIgnoreCase(userTypeName)) {
            RecruiterProfile profile = findRecruiterProfileByUserId(currentUser.getUserId());
            return userMapper.toUserProfileDto(profile);
        } else {
            JobSeekerProfile profile = findJobSeekerProfileByUserId(currentUser.getUserId());
            return userMapper.toUserProfileDto(profile);
        }
    }

    @Transactional
    public UserProfileDto updateRecruiterProfile(UpdateRecruiterProfileDto dto) {
        User currentUser = userService.getCurrentAuthenticatedUser();
        RecruiterProfile profile = findRecruiterProfileByUserId(currentUser.getUserId());

        userMapper.updateRecruiterProfileFromDto(dto, profile);
        RecruiterProfile updatedProfile = recruiterProfileRepository.save(profile);

        return userMapper.toUserProfileDto(updatedProfile);
    }

    @Transactional
    public UserProfileDto updateJobSeekerProfile(UpdateJobSeekerProfileDto dto) {
        User currentUser = userService.getCurrentAuthenticatedUser();
        JobSeekerProfile profile = findJobSeekerProfileByUserId(currentUser.getUserId());

        userMapper.updateJobSeekerProfileFromDto(dto, profile);
        JobSeekerProfile updatedProfile = jobSeekerProfileRepository.save(profile);

        return userMapper.toUserProfileDto(updatedProfile);
    }

    private RecruiterProfile findRecruiterProfileByUserId(Long userId) {
        return recruiterProfileRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Perfil de recrutador não encontrado para o usuário com ID: " + userId));
    }

    private JobSeekerProfile findJobSeekerProfileByUserId(Long userId) {
        return jobSeekerProfileRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Perfil de candidato não encontrado para o usuário com ID: " + userId));
    }

    public JobSeekerProfile getCurrentJobSeekerProfile() {
        User currentUser = userService.getCurrentAuthenticatedUser();
        String userTypeName = currentUser.getUserType().getUserTypeName();

        if (!"Job Seeker".equalsIgnoreCase(userTypeName)) {
            throw new IllegalStateException("Ação não permitida para o tipo de usuário: " + userTypeName);
        }

        return jobSeekerProfileRepository.findById(currentUser.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Perfil de candidato não encontrado para o usuário: " + currentUser.getEmail()));
    }
}
