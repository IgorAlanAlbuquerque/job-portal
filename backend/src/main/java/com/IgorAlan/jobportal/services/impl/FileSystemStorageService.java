package com.IgorAlan.jobportal.services.impl;

import com.IgorAlan.jobportal.exception.StorageException;
import com.IgorAlan.jobportal.exception.ResourceNotFoundException;
import com.IgorAlan.jobportal.models.JobSeekerProfile;
import com.IgorAlan.jobportal.models.User;
import com.IgorAlan.jobportal.repository.JobSeekerProfileRepository;
import com.IgorAlan.jobportal.services.StorageService;
import com.IgorAlan.jobportal.services.UserService;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
public class FileSystemStorageService implements StorageService {

    @Value("${storage.location:upload-dir}")
    private String uploadDir;

    private final UserService userService;
    private final JobSeekerProfileRepository jobSeekerProfileRepository;

    public FileSystemStorageService(UserService userService, JobSeekerProfileRepository jobSeekerProfileRepository) {
        this.userService = userService;
        this.jobSeekerProfileRepository = jobSeekerProfileRepository;
    }

    @Override
    public void init() {
        try {
            Files.createDirectories(Paths.get(uploadDir));
        } catch (IOException e) {
            throw new StorageException("Não foi possível inicializar o diretório de armazenamento.", e);
        }
    }

    @Override
    public String saveProfilePhoto(MultipartFile file) {
        User currentUser = userService.getCurrentAuthenticatedUser();
        String extension = StringUtils.getFilenameExtension(file.getOriginalFilename());
        String uniqueFilename = UUID.randomUUID().toString() + "." + extension;
        
        Path userDirPath = Paths.get(uploadDir, "photos", "candidate", String.valueOf(currentUser.getUserId()));
        return saveFile(file, userDirPath, uniqueFilename);
    }

    @Override
    @Transactional
    public String saveResume(MultipartFile file) {
        User currentUser = userService.getCurrentAuthenticatedUser();
        JobSeekerProfile profile = jobSeekerProfileRepository.findById(currentUser.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("Perfil de candidato não encontrado"));

        String extension = StringUtils.getFilenameExtension(file.getOriginalFilename());
        String uniqueFilename = "resume_" + currentUser.getUserId() + "_" + UUID.randomUUID().toString() + "." + extension;

        Path userDirPath = Paths.get(uploadDir, "resumes", String.valueOf(currentUser.getUserId()));
        saveFile(file, userDirPath, uniqueFilename);

        profile.setResume(uniqueFilename);
        jobSeekerProfileRepository.save(profile);

        return uniqueFilename;
    }

    private String saveFile(MultipartFile file, Path location, String filename) {
        if (file.isEmpty()) {
            throw new StorageException("Falha ao salvar arquivo vazio.");
        }
        try {
            Files.createDirectories(location);
            Path destinationFile = location.resolve(Paths.get(filename)).normalize().toAbsolutePath();
            try (InputStream inputStream = file.getInputStream()) {
                Files.copy(inputStream, destinationFile, StandardCopyOption.REPLACE_EXISTING);
            }
            return filename;
        } catch (IOException e) {
            throw new StorageException("Falha ao salvar o arquivo.", e);
        }
    }

    @Override
    public Resource loadResume(Long userId) {
        JobSeekerProfile profile = jobSeekerProfileRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Perfil de candidato não encontrado para o ID: " + userId));

        String filename = profile.getResume();
        if (filename == null || filename.isBlank()) {
            throw new ResourceNotFoundException("Nenhum currículo encontrado para este usuário.");
        }

        try {
            Path userDirPath = Paths.get(uploadDir, "resumes", String.valueOf(userId));
            Path file = userDirPath.resolve(filename);
            Resource resource = new UrlResource(file.toUri());

            if (resource.exists() && resource.isReadable()) {
                return resource;
            } else {
                throw new StorageException("Não foi possível ler o arquivo: " + filename);
            }
        } catch (MalformedURLException e) {
            throw new StorageException("Erro ao formar a URL para o arquivo: " + filename, e);
        }
    }
}
