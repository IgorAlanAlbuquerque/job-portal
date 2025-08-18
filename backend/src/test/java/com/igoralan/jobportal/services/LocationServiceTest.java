package com.igoralan.jobportal.services;

import com.igoralan.jobportal.exception.ResourceNotFoundException;
import com.igoralan.jobportal.models.JobLocation;
import com.igoralan.jobportal.repository.LocationRepository;
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
class LocationServiceTest {

    @Mock
    private LocationRepository locationRepository;

    @InjectMocks
    private LocationService locationService;

    @Test
    void findById_shouldReturnJobLocation_whenLocationExists() {
        Long locationId = 1L;
        JobLocation expectedLocation = new JobLocation();
        expectedLocation.setId(locationId);
        expectedLocation.setCity("São Paulo");
        expectedLocation.setState("SP");
        expectedLocation.setCountry("Brasil");

        when(locationRepository.findById(locationId)).thenReturn(Optional.of(expectedLocation));

        JobLocation actualLocation = locationService.findById(locationId);

        assertThat(actualLocation).isNotNull();
        assertThat(actualLocation.getId()).isEqualTo(expectedLocation.getId());
        assertThat(actualLocation.getCity()).isEqualTo(expectedLocation.getCity());

        verify(locationRepository).findById(locationId);
    }

    @Test
    void findById_shouldThrowResourceNotFoundException_whenLocationDoesNotExist() {
        Long locationId = 99L;

        when(locationRepository.findById(locationId)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            locationService.findById(locationId);
        });

        assertThat(exception.getMessage()).isEqualTo("Localização não encontrada com o ID: " + locationId);

        verify(locationRepository).findById(locationId);
    }
}