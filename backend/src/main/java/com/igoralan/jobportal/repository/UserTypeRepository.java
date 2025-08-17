package com.igoralan.jobportal.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.igoralan.jobportal.models.UserType;

public interface UserTypeRepository extends JpaRepository<UserType, Long> {
}
