package com.IgorAlan.jobportal.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.IgorAlan.jobportal.models.User;

import java.util.Optional;

public interface UsersRepository extends JpaRepository<User, Integer> {

    Optional<User> findByEmail(String email);

}
