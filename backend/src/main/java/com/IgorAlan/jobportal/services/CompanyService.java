package com.IgorAlan.jobportal.services;

import com.IgorAlan.jobportal.exception.ResourceNotFoundException;
import com.IgorAlan.jobportal.models.JobCompany;
import com.IgorAlan.jobportal.repository.CompanyRepository;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

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