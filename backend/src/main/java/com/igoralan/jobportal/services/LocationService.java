package com.igoralan.jobportal.services;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.igoralan.jobportal.exception.ResourceNotFoundException;
import com.igoralan.jobportal.models.JobLocation;
import com.igoralan.jobportal.repository.LocationRepository;

@Service
public class LocationService {

    private final LocationRepository locationRepository;

    public LocationService(LocationRepository locationRepository) {
        this.locationRepository = locationRepository;
    }

    @Cacheable(value = "locations", key = "#id")
    public JobLocation findById(Long id) {
        return locationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Localização não encontrada com o ID: " + id));
    }
}
