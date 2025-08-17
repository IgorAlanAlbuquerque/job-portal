package com.igoralan.jobportal.services;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

public interface StorageService {

    void init();

    String saveProfilePhoto(MultipartFile file);

    String saveResume(MultipartFile file);

    Resource loadResume(Long userId);
}
