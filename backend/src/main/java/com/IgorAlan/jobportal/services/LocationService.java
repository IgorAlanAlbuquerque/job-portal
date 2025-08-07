package com.IgorAlan.jobportal.services;

import com.IgorAlan.jobportal.exception.ResourceNotFoundException;
import com.IgorAlan.jobportal.models.JobLocation;
import com.IgorAlan.jobportal.repository.LocationRepository;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

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
