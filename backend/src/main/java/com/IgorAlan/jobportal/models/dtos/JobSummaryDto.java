package com.IgorAlan.jobportal.models.dtos;

import java.time.LocalDateTime;

public record JobSummaryDto(
        Long jobPostId,
        String jobTitle,
        String companyName,
        String city,
        String state,
        LocalDateTime postedDate) {
}
