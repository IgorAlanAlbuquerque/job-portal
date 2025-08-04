package com.IgorAlan.jobportal.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.IgorAlan.jobportal.models.UserType;

public interface UserTypeRepository extends JpaRepository<UserType, Long> {
}
