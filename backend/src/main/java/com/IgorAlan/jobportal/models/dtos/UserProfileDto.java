package com.IgorAlan.jobportal.models.dtos;

public record UserProfileDto(
        Long userId,
        String email,
        String firstName,
        String lastName,
        String city,
        String state,
        String profilePhotoUrl,
        String userType) {
}
