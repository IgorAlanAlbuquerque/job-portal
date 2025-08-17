package com.igoralan.jobportal.models.dtos;

import com.igoralan.jobportal.models.JobCompany;
import com.igoralan.jobportal.models.JobLocation;

public record RecruiterJobsDto(
                Long totalCandidates,
                Long jobPostId,
                String jobTitle,
                JobLocation jobLocation,
                JobCompany jobCompany) {
}
