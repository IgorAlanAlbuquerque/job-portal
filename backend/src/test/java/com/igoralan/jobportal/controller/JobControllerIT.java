package com.igoralan.jobportal.controller;

import com.igoralan.jobportal.models.dtos.JobSummaryDto;
import com.igoralan.jobportal.services.JobService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class JobControllerIT {

    @Autowired
    MockMvc mockMvc;

    @MockitoBean
    JobService jobService;

    // ---------- GET /api/jobs/my-jobs ----------
    @Test
    @DisplayName("GET /api/jobs/my-jobs (Recruiter) deve retornar 200")
    @WithMockUser(authorities = "Recruiter")
    void getRecruiterJobs_ok() throws Exception {
        when(jobService.getRecruiterJobs()).thenReturn(List.of());

        mockMvc.perform(get("/api/jobs/my-jobs"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith("application/json"))
                .andExpect(jsonPath("$", hasSize(0)));

        verify(jobService).getRecruiterJobs();
    }

    @Test
    @DisplayName("GET /api/jobs/my-jobs não autenticado deve retornar 401")
    void getRecruiterJobs_unauthenticated_401() throws Exception {
        mockMvc.perform(get("/api/jobs/my-jobs"))
                .andExpect(status().isUnauthorized());

        verifyNoInteractions(jobService);
    }

    @Test
    @DisplayName("GET /api/jobs/my-jobs autenticado sem permissão deve retornar 403")
    @WithMockUser(authorities = "JobSeeker")
    void getRecruiterJobs_forbidden_403() throws Exception {
        mockMvc.perform(get("/api/jobs/my-jobs"))
                .andExpect(status().isForbidden());

        verifyNoInteractions(jobService);
    }

    // ---------- POST /api/jobs/ ----------
    @Test
    @DisplayName("POST /api/jobs/ (Recruiter) deve criar e retornar 201")
    @WithMockUser(authorities = "Recruiter")
    void createNewJob_ok() throws Exception {
        when(jobService.createNewJob(any())).thenReturn(null);

        mockMvc.perform(post("/api/jobs/")
                .contentType("application/json")
                .content("{}"))
                .andExpect(status().isCreated());

        verify(jobService).createNewJob(any());
    }

    @Test
    @DisplayName("POST /api/jobs/ não autenticado deve retornar 401")
    void createNewJob_unauthenticated_401() throws Exception {
        mockMvc.perform(post("/api/jobs/")
                .contentType("application/json")
                .content("{}"))
                .andExpect(status().isUnauthorized());

        verifyNoInteractions(jobService);
    }

    @Test
    @DisplayName("POST /api/jobs/ autenticado sem permissão deve retornar 403")
    @WithMockUser(authorities = "JobSeeker")
    void createNewJob_forbidden_403() throws Exception {
        mockMvc.perform(post("/api/jobs/")
                .contentType("application/json")
                .content("{}"))
                .andExpect(status().isForbidden());

        verifyNoInteractions(jobService);
    }

    // ---------- GET /api/jobs/{id} ----------
    @Test
    @DisplayName("GET /api/jobs/{id} deve retornar 200 (público)")
    @WithMockUser(authorities = "JobSeeker")
    void getJobDetails_ok() throws Exception {
        when(jobService.getJobDetails(42L)).thenReturn(null);

        mockMvc.perform(get("/api/jobs/{id}", 42L))
                .andExpect(status().isOk());

        verify(jobService).getJobDetails(42L);
    }

    // ---------- GET /api/jobs/search ----------
    @Test
    @DisplayName("GET /api/jobs/search deve retornar Page<JobSummaryDto> com 2 itens")
    @WithMockUser(authorities = "JobSeeker")
    void searchJobs_ok() throws Exception {
        JobSummaryDto j1 = new JobSummaryDto(
                1L, "Engenheiro de Software", "Tech Corp", "São Paulo", "SP", LocalDateTime.now());
        JobSummaryDto j2 = new JobSummaryDto(
                2L, "Analista de Dados", "Data Inc", "Rio de Janeiro", "RJ", LocalDateTime.now());

        Page<JobSummaryDto> page = new PageImpl<>(List.of(j1, j2));

        when(jobService.searchJobs(eq("java"), eq("SP"), any())).thenReturn(page);

        mockMvc.perform(get("/api/jobs/search")
                .param("keyword", "java")
                .param("location", "SP"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(2)))
                .andExpect(jsonPath("$.content[0].jobTitle").value("Engenheiro de Software"))
                .andExpect(jsonPath("$.content[1].companyName").value("Data Inc"));

        verify(jobService).searchJobs(eq("java"), eq("SP"), any());
    }

    // ---------- GET /api/jobs/{id}/applicants ----------
    @Test
    @DisplayName("GET /api/jobs/{id}/applicants (Recruiter) deve retornar 200")
    @WithMockUser(authorities = "Recruiter")
    void getJobApplicants_ok() throws Exception {
        when(jobService.getJobApplicants(10L)).thenReturn(List.of());

        mockMvc.perform(get("/api/jobs/{id}/applicants", 10L))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith("application/json"))
                .andExpect(jsonPath("$", hasSize(0)));

        verify(jobService).getJobApplicants(10L);
    }

    @Test
    @DisplayName("GET /api/jobs/{id}/applicants não autenticado deve retornar 401")
    void getJobApplicants_unauthenticated_401() throws Exception {
        mockMvc.perform(get("/api/jobs/{id}/applicants", 10L))
                .andExpect(status().isUnauthorized());

        verifyNoInteractions(jobService);
    }

    @Test
    @DisplayName("GET /api/jobs/{id}/applicants autenticado sem permissão deve retornar 403")
    @WithMockUser(authorities = "JobSeeker")
    void getJobApplicants_forbidden_403() throws Exception {
        mockMvc.perform(get("/api/jobs/{id}/applicants", 10L))
                .andExpect(status().isForbidden());

        verifyNoInteractions(jobService);
    }

    // ---------- DELETE /api/jobs/{id} ----------
    @Test
    @DisplayName("DELETE /api/jobs/{id} (Recruiter) deve retornar 204")
    @WithMockUser(authorities = "Recruiter")
    void deleteJob_ok() throws Exception {
        doNothing().when(jobService).deleteJob(55L);

        mockMvc.perform(delete("/api/jobs/{id}", 55L))
                .andExpect(status().isNoContent());

        verify(jobService).deleteJob(55L);
    }

    @Test
    @DisplayName("DELETE /api/jobs/{id} não autenticado deve retornar 401")
    void deleteJob_unauthenticated_401() throws Exception {
        mockMvc.perform(delete("/api/jobs/{id}", 55L))
                .andExpect(status().isUnauthorized());

        verifyNoInteractions(jobService);
    }

    @Test
    @DisplayName("DELETE /api/jobs/{id} autenticado sem permissão deve retornar 403")
    @WithMockUser(authorities = "JobSeeker")
    void deleteJob_forbidden_403() throws Exception {
        mockMvc.perform(delete("/api/jobs/{id}", 55L))
                .andExpect(status().isForbidden());

        verifyNoInteractions(jobService);
    }

    // ---------- PUT /api/jobs/{id} ----------
    @Test
    @DisplayName("PUT /api/jobs/{id} (Recruiter) deve retornar 200")
    @WithMockUser(authorities = "Recruiter")
    void updateJob_ok() throws Exception {
        when(jobService.updateJob(eq(77L), any())).thenReturn(null);

        mockMvc.perform(put("/api/jobs/{id}", 77L)
                .contentType("application/json")
                .content("{}"))
                .andExpect(status().isOk());

        verify(jobService).updateJob(eq(77L), any());
    }

    @Test
    @DisplayName("PUT /api/jobs/{id} não autenticado deve retornar 401")
    void updateJob_unauthenticated_401() throws Exception {
        mockMvc.perform(put("/api/jobs/{id}", 77L)
                .contentType("application/json")
                .content("{}"))
                .andExpect(status().isUnauthorized());

        verifyNoInteractions(jobService);
    }

    @Test
    @DisplayName("PUT /api/jobs/{id} autenticado sem permissão deve retornar 403")
    @WithMockUser(authorities = "JobSeeker")
    void updateJob_forbidden_403() throws Exception {
        mockMvc.perform(put("/api/jobs/{id}", 77L)
                .contentType("application/json")
                .content("{}"))
                .andExpect(status().isForbidden());

        verifyNoInteractions(jobService);
    }
}
