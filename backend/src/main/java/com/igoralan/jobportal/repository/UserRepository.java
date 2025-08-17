package com.igoralan.jobportal.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.igoralan.jobportal.models.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

}
