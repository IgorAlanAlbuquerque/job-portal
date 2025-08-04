package com.IgorAlan.jobportal.models.dtos;

import java.time.LocalDateTime;

public record JobDetailDto(
    Long jobPostId,
    String jobTitle,
    String description,
    String companyName,
    String companyLogoUrl,
    String city,
    String state,
    String country,
    String jobType,
    String salary,
    String remote,
    LocalDateTime postedDate,
    String recruiterName,
    boolean isSaved,
    boolean isApplied
) {}
