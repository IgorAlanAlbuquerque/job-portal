package com.IgorAlan.jobportal.repository;

import com.IgorAlan.jobportal.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UsersRepository extends JpaRepository<Users, Integer> {
}
