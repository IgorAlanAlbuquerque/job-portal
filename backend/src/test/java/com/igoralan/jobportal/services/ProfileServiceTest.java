package com.igoralan.jobportal.services;

import com.igoralan.jobportal.exception.ResourceNotFoundException;
import com.igoralan.jobportal.mapper.UserMapper;
import com.igoralan.jobportal.models.*;
import com.igoralan.jobportal.models.dtos.UpdateJobSeekerProfileDto;
import com.igoralan.jobportal.models.dtos.UserProfileDto;
import com.igoralan.jobportal.repository.JobSeekerProfileRepository;
import com.igoralan.jobportal.repository.RecruiterProfileRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProfileServiceTest {

    @Mock
    private JobSeekerProfileRepository jobSeekerProfileRepository;

    @Mock
    private RecruiterProfileRepository recruiterProfileRepository;

    @Mock
    private UserService userService;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private ProfileService profileService;

    private User recruiterUser;
    private User jobSeekerUser;

    @BeforeEach
    void setUp() {
        UserType recruiterType = new UserType();
        recruiterType.setUserTypeName("Recruiter");

        recruiterUser = new User();
        recruiterUser.setUserId(1L);
        recruiterUser.setEmail("recruiter@test.com");
        recruiterUser.setPassword("pass");
        recruiterUser.setUserType(recruiterType);

        UserType jobSeekerType = new UserType();
        jobSeekerType.setUserTypeName("Job Seeker");

        jobSeekerUser = new User();
        jobSeekerUser.setUserId(2L);
        jobSeekerUser.setEmail("jobseeker@test.com");
        jobSeekerUser.setPassword("pass");
        jobSeekerUser.setUserType(jobSeekerType);
    }

    @Test
    void createProfileForNewUser_shouldSaveRecruiterProfile_whenUserIsRecruiter() {
        profileService.createProfileForNewUser(recruiterUser);

        verify(recruiterProfileRepository).save(any(RecruiterProfile.class));
        verify(jobSeekerProfileRepository, never()).save(any());
    }

    @Test
    void createProfileForNewUser_shouldSaveJobSeekerProfile_whenUserIsNotRecruiter() {
        profileService.createProfileForNewUser(jobSeekerUser);

        verify(jobSeekerProfileRepository).save(any(JobSeekerProfile.class));
        verify(recruiterProfileRepository, never()).save(any());
    }

    @Test
    void getCurrentUserProfile_shouldReturnRecruiterDto_whenUserIsRecruiter() {
        RecruiterProfile profile = new RecruiterProfile(recruiterUser);
        UserProfileDto expectedDto = new UserProfileDto(
                1L,
                "recruiter@test.com",
                "John",
                "Doe",
                "São Paulo",
                "SP",
                "url/da/foto.png",
                "Recruiter");
        when(userService.getCurrentAuthenticatedUser()).thenReturn(recruiterUser);
        when(recruiterProfileRepository.findById(recruiterUser.getUserId())).thenReturn(Optional.of(profile));
        when(userMapper.toUserProfileDto(profile)).thenReturn(expectedDto);

        UserProfileDto result = profileService.getCurrentUserProfile();

        assertThat(result).isEqualTo(expectedDto);
        verify(recruiterProfileRepository).findById(recruiterUser.getUserId());
        verify(jobSeekerProfileRepository, never()).findById(any());
    }

    @Test
    void getCurrentUserProfile_shouldReturnJobSeekerDto_whenUserIsJobSeeker() {
        JobSeekerProfile profile = new JobSeekerProfile(jobSeekerUser);
        UserProfileDto expectedDto = new UserProfileDto(
                jobSeekerUser.getUserId(),
                jobSeekerUser.getEmail(),
                "Jane",
                "Seeker",
                "Rio de Janeiro",
                "RJ",
                "http://example.com/photo.jpg",
                "Job Seeker");
        when(userService.getCurrentAuthenticatedUser()).thenReturn(jobSeekerUser);
        when(jobSeekerProfileRepository.findById(jobSeekerUser.getUserId())).thenReturn(Optional.of(profile));
        when(userMapper.toUserProfileDto(profile)).thenReturn(expectedDto);

        UserProfileDto result = profileService.getCurrentUserProfile();

        assertThat(result).isEqualTo(expectedDto);
        verify(jobSeekerProfileRepository).findById(jobSeekerUser.getUserId());
        verify(recruiterProfileRepository, never()).findById(any());
    }

    @Test
    void updateJobSeekerProfile_shouldUpdateAndReturnDto() {
        UpdateJobSeekerProfileDto dto = new UpdateJobSeekerProfileDto(
                "Jane",
                "Seeker Updated",
                "Rio de Janeiro",
                "RJ",
                "Brasil",
                "Desenvolvedora Java",
                "Resumo profissional...",
                "http://github.com/jane",
                "http://linkedin.com/in/jane/resume.pdf");

        JobSeekerProfile profile = new JobSeekerProfile(jobSeekerUser);
        when(userService.getCurrentAuthenticatedUser()).thenReturn(jobSeekerUser);
        when(jobSeekerProfileRepository.findById(jobSeekerUser.getUserId())).thenReturn(Optional.of(profile));
        when(jobSeekerProfileRepository.save(any(JobSeekerProfile.class))).thenReturn(profile);

        profileService.updateJobSeekerProfile(dto);

        verify(userMapper).updateJobSeekerProfileFromDto(dto, profile);
        verify(jobSeekerProfileRepository).save(profile);
        verify(userMapper).toUserProfileDto(profile);
    }

    @Test
    void getCurrentJobSeekerProfile_shouldReturnProfile_whenUserIsJobSeeker() {
        JobSeekerProfile expectedProfile = new JobSeekerProfile(jobSeekerUser);
        when(userService.getCurrentAuthenticatedUser()).thenReturn(jobSeekerUser);
        when(jobSeekerProfileRepository.findById(jobSeekerUser.getUserId())).thenReturn(Optional.of(expectedProfile));

        JobSeekerProfile result = profileService.getCurrentJobSeekerProfile();

        assertThat(result).isEqualTo(expectedProfile);
    }

    @Test
    void getCurrentJobSeekerProfile_shouldThrowIllegalStateException_whenUserIsNotJobSeeker() {
        when(userService.getCurrentAuthenticatedUser()).thenReturn(recruiterUser);

        assertThrows(IllegalStateException.class, () -> {
            profileService.getCurrentJobSeekerProfile();
        });
    }

    @Test
    void getCurrentJobSeekerProfile_shouldThrowResourceNotFoundException_whenProfileDoesNotExist() {
        when(userService.getCurrentAuthenticatedUser()).thenReturn(jobSeekerUser);
        when(jobSeekerProfileRepository.findById(jobSeekerUser.getUserId())).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            profileService.getCurrentJobSeekerProfile();
        });
    }
}