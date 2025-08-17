package com.igoralan.jobportal.services;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.igoralan.jobportal.exception.ResourceNotFoundException;
import com.igoralan.jobportal.models.JobCompany;
import com.igoralan.jobportal.repository.CompanyRepository;

@Service
public class CompanyService {

    private final CompanyRepository companyRepository;

    public CompanyService(CompanyRepository companyRepository) {
        this.companyRepository = companyRepository;
    }

    @Cacheable(value = "companies", key = "#id")
    public JobCompany findById(Long id) {
        return companyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Empresa não encontrada com o ID: " + id));
    }
}