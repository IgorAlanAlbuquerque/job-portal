package com.igoralan.jobportal.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.igoralan.jobportal.models.dtos.UpdateJobSeekerProfileDto;
import com.igoralan.jobportal.models.dtos.UpdateRecruiterProfileDto;
import com.igoralan.jobportal.models.dtos.UserProfileDto;
import com.igoralan.jobportal.services.ProfileService;
import com.igoralan.jobportal.services.StorageService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class ProfileControllerIT {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockitoBean
    ProfileService profileService;

    @MockitoBean
    StorageService storageService;

    // ---------- GET /api/profiles/me ----------
    @Test
    @DisplayName("GET /api/profiles/me deve retornar o perfil atual (200)")
    @WithMockUser(username = "user@test.com", authorities = { "JobSeeker" })
    void getCurrentUserProfile_ok() throws Exception {
        UserProfileDto dto = new UserProfileDto(
                10L,
                "user@test.com",
                "Ana",
                "Silva",
                null,
                null,
                null,
                "JobSeeker");

        given(profileService.getCurrentUserProfile()).willReturn(dto);

        mockMvc.perform(get("/api/profiles/me"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.userId").value(10))
                .andExpect(jsonPath("$.email").value("user@test.com"))
                .andExpect(jsonPath("$.firstName").value("Ana"))
                .andExpect(jsonPath("$.lastName").value("Silva"))
                .andExpect(jsonPath("$.userType").value("JobSeeker"));
    }

    @Test
    @DisplayName("GET /api/profiles/me sem autenticação deve retornar 401")
    void getCurrentUserProfile_unauthenticated_401() throws Exception {
        mockMvc.perform(get("/api/profiles/me"))
                .andExpect(status().isUnauthorized());
        verifyNoInteractions(profileService);
    }

    // ---------- PUT /api/profiles/recruiter ----------
    @Test
    @DisplayName("PUT /api/profiles/recruiter (Recruiter) deve atualizar e retornar 200")
    @WithMockUser(authorities = { "Recruiter" })
    void updateRecruiterProfile_ok() throws Exception {
        UpdateRecruiterProfileDto req = new UpdateRecruiterProfileDto(
                "João",
                "Pereira",
                "São Paulo",
                "SP",
                "BR",
                "https://cdn/img.png");

        UserProfileDto resp = new UserProfileDto(
                20L,
                "recruiter@test.com",
                "João",
                "Pereira",
                "São Paulo",
                "SP",
                "https://cdn/img.png",
                "Recruiter");

        given(profileService.updateRecruiterProfile(any(UpdateRecruiterProfileDto.class)))
                .willReturn(resp);

        mockMvc.perform(put("/api/profiles/recruiter")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req))
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.userId").value(20))
                .andExpect(jsonPath("$.email").value("recruiter@test.com"))
                .andExpect(jsonPath("$.firstName").value("João"))
                .andExpect(jsonPath("$.lastName").value("Pereira"))
                .andExpect(jsonPath("$.city").value("São Paulo"))
                .andExpect(jsonPath("$.state").value("SP"))
                .andExpect(jsonPath("$.profilePhotoUrl").value("https://cdn/img.png"))
                .andExpect(jsonPath("$.userType").value("Recruiter"));

        ArgumentCaptor<UpdateRecruiterProfileDto> captor = ArgumentCaptor.forClass(UpdateRecruiterProfileDto.class);
        verify(profileService).updateRecruiterProfile(captor.capture());
    }

    @Test
    @DisplayName("PUT /api/profiles/recruiter sem permissão deve retornar 403")
    @WithMockUser(authorities = { "JobSeeker" })
    void updateRecruiterProfile_forbidden_403() throws Exception {
        UpdateRecruiterProfileDto req = new UpdateRecruiterProfileDto(
                "João",
                "Pereira",
                "São Paulo",
                "SP",
                "BR",
                "https://cdn/img.png");

        mockMvc.perform(put("/api/profiles/recruiter")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req))
                .with(csrf()))
                .andExpect(status().isForbidden());

        verifyNoInteractions(profileService);
    }

    @Test
    @DisplayName("PUT /api/profiles/recruiter não autenticado deve retornar 401")
    void updateRecruiterProfile_unauthenticated_401() throws Exception {
        UpdateRecruiterProfileDto req = new UpdateRecruiterProfileDto(
                "João",
                "Pereira",
                "São Paulo",
                "SP",
                "BR",
                "https://cdn/img.png");

        mockMvc.perform(put("/api/profiles/recruiter")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req))
                .with(csrf()))
                .andExpect(status().isUnauthorized());

        verifyNoInteractions(profileService);
    }

    // ---------- PUT /api/profiles/job-seeker ----------
    @Test
    @DisplayName("PUT /api/profiles/job-seeker (JobSeeker) deve atualizar e retornar 200")
    @WithMockUser(authorities = { "JobSeeker" })
    void updateJobSeekerProfile_ok() throws Exception {
        UpdateJobSeekerProfileDto req = new UpdateJobSeekerProfileDto(
                "Maria",
                "Souza",
                "São Paulo",
                "SP",
                "BR",
                "CLT",
                "FULL_TIME",
                "resume.pdf",
                "https://cdn/img.png");

        UserProfileDto resp = new UserProfileDto(
                30L,
                "dev@test.com",
                "Maria",
                "Souza",
                "São Paulo",
                "SP",
                "https://cdn/img.png",
                "JobSeeker");

        given(profileService.updateJobSeekerProfile(any(UpdateJobSeekerProfileDto.class)))
                .willReturn(resp);

        mockMvc.perform(put("/api/profiles/job-seeker")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req))
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.userId").value(30))
                .andExpect(jsonPath("$.email").value("dev@test.com"))
                .andExpect(jsonPath("$.firstName").value("Maria"))
                .andExpect(jsonPath("$.lastName").value("Souza"))
                .andExpect(jsonPath("$.city").value("São Paulo"))
                .andExpect(jsonPath("$.state").value("SP"))
                .andExpect(jsonPath("$.profilePhotoUrl").value("https://cdn/img.png"))
                .andExpect(jsonPath("$.userType").value("JobSeeker"));

        verify(profileService).updateJobSeekerProfile(any(UpdateJobSeekerProfileDto.class));
    }

    @Test
    @DisplayName("PUT /api/profiles/job-seeker sem permissão deve retornar 403")
    @WithMockUser(authorities = { "Recruiter" })
    void updateJobSeekerProfile_forbidden_403() throws Exception {
        UpdateJobSeekerProfileDto req = new UpdateJobSeekerProfileDto(
                "Maria",
                "Souza",
                "São Paulo",
                "SP",
                "BR",
                "CLT",
                "FULL_TIME",
                "resume.pdf",
                "https://cdn/img.png");

        mockMvc.perform(put("/api/profiles/job-seeker")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req))
                .with(csrf()))
                .andExpect(status().isForbidden());

        verifyNoInteractions(profileService);
    }

    @Test
    @DisplayName("PUT /api/profiles/job-seeker não autenticado deve retornar 401")
    void updateJobSeekerProfile_unauthenticated_401() throws Exception {
        UpdateJobSeekerProfileDto req = new UpdateJobSeekerProfileDto(
                "Maria",
                "Souza",
                "São Paulo",
                "SP",
                "BR",
                "CLT",
                "FULL_TIME",
                "resume.pdf",
                "https://cdn/img.png");

        mockMvc.perform(put("/api/profiles/job-seeker")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req))
                .with(csrf()))
                .andExpect(status().isUnauthorized());

        verifyNoInteractions(profileService);
    }

    // ---------- POST /api/profiles/me/photo ----------
    @Test
    @DisplayName("POST /api/profiles/me/photo deve aceitar upload e retornar 200")
    @WithMockUser
    void uploadProfilePhoto_ok() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "image", "photo.png", "image/png", "img-bytes".getBytes());

        mockMvc.perform(multipart("/api/profiles/me/photo")
                .file(file)
                .with(csrf()))
                .andExpect(status().isOk());

        ArgumentCaptor<org.springframework.web.multipart.MultipartFile> captor = ArgumentCaptor
                .forClass(org.springframework.web.multipart.MultipartFile.class);
        verify(storageService).saveProfilePhoto(captor.capture());
    }

    // ---------- POST /api/profiles/job-seeker/resume ----------
    @Test
    @DisplayName("POST /api/profiles/job-seeker/resume (JobSeeker) deve aceitar upload e retornar 200")
    @WithMockUser(authorities = { "JobSeeker" })
    void uploadResume_ok() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "resume", "cv.pdf", "application/pdf", "pdf-bytes".getBytes());

        mockMvc.perform(multipart("/api/profiles/job-seeker/resume")
                .file(file)
                .with(csrf()))
                .andExpect(status().isOk());

        verify(storageService).saveResume(any());
    }

    @Test
    @DisplayName("POST /api/profiles/job-seeker/resume sem permissão deve retornar 403")
    @WithMockUser(authorities = { "Recruiter" })
    void uploadResume_forbidden_403() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "resume", "cv.pdf", "application/pdf", "pdf-bytes".getBytes());

        mockMvc.perform(multipart("/api/profiles/job-seeker/resume")
                .file(file)
                .with(csrf()))
                .andExpect(status().isForbidden());

        verifyNoInteractions(storageService);
    }

    @Test
    @DisplayName("POST /api/profiles/job-seeker/resume não autenticado deve retornar 401")
    void uploadResume_unauthenticated_401() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "resume", "cv.pdf", "application/pdf", "pdf-bytes".getBytes());

        mockMvc.perform(multipart("/api/profiles/job-seeker/resume")
                .file(file)
                .with(csrf()))
                .andExpect(status().isUnauthorized());

        verifyNoInteractions(storageService);
    }

    // ---------- GET /api/profiles/{userId}/resume ----------
    @Test
    @DisplayName("GET /api/profiles/{id}/resume (Recruiter) deve baixar arquivo com headers corretos (200)")
    @WithMockUser(authorities = { "Recruiter" })
    void downloadResume_ok() throws Exception {
        byte[] content = "pdf-file".getBytes();
        Resource resource = new ByteArrayResource(content) {
            @Override
            public String getFilename() {
                return "resume.pdf";
            }
        };
        given(storageService.loadResume(99L)).willReturn(resource);

        mockMvc.perform(get("/api/profiles/{userId}/resume", 99L))
                .andExpect(status().isOk())
                .andExpect(
                        header().string("Content-Disposition", containsString("attachment; filename=\"resume.pdf\"")))
                .andExpect(content().contentType("application/octet-stream"))
                .andExpect(content().bytes(content));

        verify(storageService).loadResume(99L);
    }

    @Test
    @DisplayName("GET /api/profiles/{id}/resume sem permissão deve retornar 403")
    @WithMockUser(authorities = { "JobSeeker" })
    void downloadResume_forbidden_403() throws Exception {
        mockMvc.perform(get("/api/profiles/{userId}/resume", 77L))
                .andExpect(status().isForbidden());

        verifyNoInteractions(storageService);
    }

    @Test
    @DisplayName("GET /api/profiles/{id}/resume não autenticado deve retornar 401")
    void downloadResume_unauthenticated_401() throws Exception {
        mockMvc.perform(get("/api/profiles/{userId}/resume", 77L))
                .andExpect(status().isUnauthorized());

        verifyNoInteractions(storageService);
    }
}
