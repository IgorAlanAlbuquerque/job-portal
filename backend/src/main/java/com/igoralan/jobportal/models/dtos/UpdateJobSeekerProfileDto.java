package com.igoralan.jobportal.models.dtos;

public record UpdateJobSeekerProfileDto(
                String firstName,
                String lastName,
                String city,
                String state,
                String country,
                String workAuthorization,
                String employmentType,
                String resume,
                String profilePhoto) {
}
