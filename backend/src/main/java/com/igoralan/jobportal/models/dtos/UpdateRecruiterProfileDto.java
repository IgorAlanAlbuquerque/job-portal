package com.igoralan.jobportal.models.dtos;

public record UpdateRecruiterProfileDto(
                String firstName,
                String lastName,
                String city,
                String state,
                String country,
                String profilePhoto) {
}