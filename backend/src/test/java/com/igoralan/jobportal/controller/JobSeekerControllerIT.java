package com.igoralan.jobportal.controller;

import com.igoralan.jobportal.models.dtos.JobSummaryDto;
import com.igoralan.jobportal.services.JobSeekerService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class JobSeekerControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private JobSeekerService jobSeekerService;

    @Test
    @WithMockUser(authorities = "JobSeeker")
    void applyToJob_whenUserIsJobSeeker_shouldReturnCreated() throws Exception {
        long jobId = 1L;
        doNothing().when(jobSeekerService).applyToJob(jobId);

        mockMvc.perform(post("/api/job-seeker/apply/{jobId}", jobId))
                .andExpect(status().isCreated());

        verify(jobSeekerService).applyToJob(jobId);
    }

    @Test
    @WithMockUser(authorities = "Recruiter")
    void applyToJob_whenUserIsNotJobSeeker_shouldReturnForbidden() throws Exception {
        long jobId = 1L;

        mockMvc.perform(post("/api/job-seeker/apply/{jobId}", jobId))
                .andExpect(status().isForbidden());

        verify(jobSeekerService, never()).applyToJob(anyLong());
    }

    @Test
    @WithMockUser(authorities = "JobSeeker")
    void saveJob_whenUserIsJobSeeker_shouldReturnCreated() throws Exception {
        long jobId = 2L;
        doNothing().when(jobSeekerService).saveJob(jobId);

        mockMvc.perform(post("/api/job-seeker/save/{jobId}", jobId))
                .andExpect(status().isCreated());

        verify(jobSeekerService).saveJob(jobId);
    }

    @Test
    @WithMockUser(authorities = "JobSeeker")
    void getSavedJobs_whenUserIsJobSeeker_shouldReturnOkAndListOfJobs() throws Exception {
        JobSummaryDto job1 = new JobSummaryDto(1L, "Engenheiro de Software", "Tech Corp", "São Paulo", "SP",
                LocalDateTime.now());
        JobSummaryDto job2 = new JobSummaryDto(2L, "Analista de Dados", "Data Inc", "Rio de Janeiro", "RJ",
                LocalDateTime.now());
        List<JobSummaryDto> savedJobsList = List.of(job1, job2);

        when(jobSeekerService.getSavedJobsForCurrentUser()).thenReturn(savedJobsList);

        mockMvc.perform(get("/api/job-seeker/saved-jobs"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].jobTitle").value("Engenheiro de Software"))
                .andExpect(jsonPath("$[1].companyName").value("Data Inc"));
    }

    @Test
    void getSavedJobs_whenUserIsUnauthenticated_shouldReturnForbidden() throws Exception {
        mockMvc.perform(get("/api/job-seeker/saved-jobs"))
                .andExpect(status().isForbidden());
    }
}