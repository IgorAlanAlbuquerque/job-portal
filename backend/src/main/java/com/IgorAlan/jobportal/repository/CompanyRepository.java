package com.IgorAlan.jobportal.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.IgorAlan.jobportal.models.JobCompany;

public interface CompanyRepository extends JpaRepository<JobCompany, Long> {

}
