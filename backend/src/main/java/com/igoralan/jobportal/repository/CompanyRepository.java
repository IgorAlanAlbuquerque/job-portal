package com.igoralan.jobportal.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.igoralan.jobportal.models.JobCompany;

public interface CompanyRepository extends JpaRepository<JobCompany, Long> {

}
