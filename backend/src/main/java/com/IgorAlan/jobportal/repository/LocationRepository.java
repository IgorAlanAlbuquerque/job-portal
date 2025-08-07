package com.IgorAlan.jobportal.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.IgorAlan.jobportal.models.JobLocation;

@Repository
public interface LocationRepository extends JpaRepository<JobLocation, Long> {

}
