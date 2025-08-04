package com.IgorAlan.jobportal.repository;


import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.IgorAlan.jobportal.models.RecruiterProfile;

public interface RecruiterProfileRepository extends JpaRepository<RecruiterProfile, Integer> {

    Optional<RecruiterProfile> findById(Long id);
}
