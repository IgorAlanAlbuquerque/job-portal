package com.igoralan.jobportal.services;

import com.igoralan.jobportal.exception.ResourceNotFoundException;
import com.igoralan.jobportal.models.JobCompany;
import com.igoralan.jobportal.repository.CompanyRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CompanyServiceTest {

    @Mock
    private CompanyRepository companyRepository;

    @InjectMocks
    private CompanyService companyService;

    @Test
    void findById_shouldReturnJobCompany_whenCompanyExists() {
        Long companyId = 1L;
        JobCompany expectedCompany = new JobCompany();
        expectedCompany.setId(companyId);
        expectedCompany.setName("Tech Corp");

        when(companyRepository.findById(companyId)).thenReturn(Optional.of(expectedCompany));

        JobCompany actualCompany = companyService.findById(companyId);

        assertThat(actualCompany).isNotNull();
        assertThat(actualCompany.getId()).isEqualTo(expectedCompany.getId());
        assertThat(actualCompany.getName()).isEqualTo(expectedCompany.getName());

        verify(companyRepository, times(1)).findById(companyId);
    }

    @Test
    void findById_shouldThrowResourceNotFoundException_whenCompanyDoesNotExist() {
        Long companyId = 99L;

        when(companyRepository.findById(companyId)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            companyService.findById(companyId);
        });

        assertThat(exception.getMessage()).isEqualTo("Empresa não encontrada com o ID: " + companyId);

        verify(companyRepository, times(1)).findById(companyId);
    }
}
