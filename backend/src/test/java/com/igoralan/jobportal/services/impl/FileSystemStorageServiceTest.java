package com.igoralan.jobportal.services.impl;

import com.igoralan.jobportal.exception.ResourceNotFoundException;
import com.igoralan.jobportal.exception.StorageException;
import com.igoralan.jobportal.models.JobSeekerProfile;
import com.igoralan.jobportal.models.User;
import com.igoralan.jobportal.repository.JobSeekerProfileRepository;
import com.igoralan.jobportal.services.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.Resource;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FileSystemStorageServiceTest {

    @TempDir
    Path tempDir;

    @Mock
    private UserService userService;
    @Mock
    private JobSeekerProfileRepository jobSeekerProfileRepository;

    @InjectMocks
    private FileSystemStorageService storageService;

    private User mockUser;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(storageService, "uploadDir", tempDir.toString());
        storageService.init();

        mockUser = new User();
        mockUser.setUserId(1L);
    }

    @Test
    void saveProfilePhoto_shouldSaveFileToCorrectUserDirectory() throws IOException {
        MockMultipartFile file = new MockMultipartFile(
                "photo",
                "avatar.png",
                "image/png",
                "fake-image-bytes".getBytes());
        when(userService.getCurrentAuthenticatedUser()).thenReturn(mockUser);

        String savedFilename = storageService.saveProfilePhoto(file);

        assertThat(savedFilename).isNotNull().endsWith(".png");

        Path expectedPath = tempDir.resolve("photos")
                .resolve("candidate")
                .resolve(String.valueOf(mockUser.getUserId()))
                .resolve(savedFilename);

        assertThat(expectedPath).exists().isRegularFile();

        String fileContent = Files.readString(expectedPath);
        assertThat(fileContent).isEqualTo("fake-image-bytes");
    }

    @Test
    void saveResume_shouldSaveFileAndUpdateProfile() {
        MockMultipartFile file = new MockMultipartFile("resume", "resume.pdf", "application/pdf",
                "test data".getBytes());
        JobSeekerProfile mockProfile = new JobSeekerProfile();
        when(jobSeekerProfileRepository.findById(mockUser.getUserId())).thenReturn(Optional.of(mockProfile));
        when(userService.getCurrentAuthenticatedUser()).thenReturn(mockUser);

        String savedFilename = storageService.saveResume(file);

        Path expectedPath = tempDir.resolve("resumes").resolve(String.valueOf(mockUser.getUserId()))
                .resolve(savedFilename);
        assertThat(expectedPath).exists().isRegularFile();

        ArgumentCaptor<JobSeekerProfile> profileCaptor = ArgumentCaptor.forClass(JobSeekerProfile.class);
        verify(jobSeekerProfileRepository).save(profileCaptor.capture());

        JobSeekerProfile capturedProfile = profileCaptor.getValue();
        assertThat(capturedProfile.getResume()).isEqualTo(savedFilename);
    }

    @Test
    void saveResume_shouldThrowException_whenProfileNotFound() {
        MockMultipartFile file = new MockMultipartFile("resume", "resume.pdf", "application/pdf",
                "test data".getBytes());
        when(jobSeekerProfileRepository.findById(mockUser.getUserId())).thenReturn(Optional.empty());
        when(userService.getCurrentAuthenticatedUser()).thenReturn(mockUser);

        assertThrows(ResourceNotFoundException.class, () -> {
            storageService.saveResume(file);
        });
        verify(jobSeekerProfileRepository, never()).save(any());
    }

    @Test
    void saveFile_shouldThrowException_whenFileIsEmpty() {
        MockMultipartFile emptyFile = new MockMultipartFile("empty", "empty.txt", "text/plain", new byte[0]);
        when(userService.getCurrentAuthenticatedUser()).thenReturn(mockUser);

        assertThrows(StorageException.class, () -> {
            storageService.saveProfilePhoto(emptyFile);
        });
    }

    @Test
    void loadResume_shouldReturnReadableResource_whenFileExists() throws IOException {
        String filename = "my-resume.pdf";
        Long userId = 1L;
        JobSeekerProfile mockProfile = new JobSeekerProfile();
        mockProfile.setResume(filename);
        when(jobSeekerProfileRepository.findById(userId)).thenReturn(Optional.of(mockProfile));

        Path resumeDir = tempDir.resolve("resumes").resolve(String.valueOf(userId));
        Files.createDirectories(resumeDir);
        Files.write(resumeDir.resolve(filename), "pdf content".getBytes());

        Resource resource = storageService.loadResume(userId);

        assertThat(resource).isNotNull();
        assertThat(resource.exists()).isTrue();
        assertThat(resource.isReadable()).isTrue();
        assertThat(resource.getFilename()).isEqualTo(filename);
    }

    @Test
    void loadResume_shouldThrowException_whenProfileHasNoResumeFilename() {
        Long userId = 1L;
        JobSeekerProfile mockProfile = new JobSeekerProfile();
        mockProfile.setResume(null); // Currículo não definido no perfil
        when(jobSeekerProfileRepository.findById(userId)).thenReturn(Optional.of(mockProfile));

        assertThrows(ResourceNotFoundException.class, () -> {
            storageService.loadResume(userId);
        });
    }

    @Test
    void loadResume_shouldThrowException_whenFileDoesNotExist() {
        Long userId = 1L;
        JobSeekerProfile mockProfile = new JobSeekerProfile();
        mockProfile.setResume("non-existent-file.pdf");
        when(jobSeekerProfileRepository.findById(userId)).thenReturn(Optional.of(mockProfile));

        assertThrows(StorageException.class, () -> {
            storageService.loadResume(userId);
        });
    }
}