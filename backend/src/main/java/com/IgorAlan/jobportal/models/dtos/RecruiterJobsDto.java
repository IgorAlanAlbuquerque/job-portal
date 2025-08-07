package com.IgorAlan.jobportal.models.dtos;

import com.IgorAlan.jobportal.models.JobCompany;
import com.IgorAlan.jobportal.models.JobLocation;

public record RecruiterJobsDto(
        Long totalCandidates,
        Long jobPostId,
        String jobTitle,
        JobLocation jobLocation,
        JobCompany jobCompany) {
}
