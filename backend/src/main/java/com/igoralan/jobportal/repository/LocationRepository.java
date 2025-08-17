package com.igoralan.jobportal.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.igoralan.jobportal.models.JobLocation;

@Repository
public interface LocationRepository extends JpaRepository<JobLocation, Long> {

}
