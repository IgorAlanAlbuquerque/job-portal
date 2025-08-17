package com.igoralan.jobportal.models.dtos;

public record UserDto(
                Long userId,
                String firstName,
                String lastName,
                String email,
                String userType) {
}
