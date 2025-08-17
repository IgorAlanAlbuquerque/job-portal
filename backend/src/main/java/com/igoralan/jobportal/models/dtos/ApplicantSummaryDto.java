package com.igoralan.jobportal.models.dtos;

import java.time.LocalDateTime;

public record ApplicantSummaryDto(
                Long userAccountId,
                String firstName,
                String lastName,
                String profilePhotoUrl,
                LocalDateTime applyDate) {
}
