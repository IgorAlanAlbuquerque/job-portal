package com.IgorAlan.jobportal.services;

import com.IgorAlan.jobportal.exception.EmailAlreadyExistsException;
import com.IgorAlan.jobportal.mapper.UserMapper;
import com.IgorAlan.jobportal.models.User;
import com.IgorAlan.jobportal.models.UserType;
import com.IgorAlan.jobportal.models.dtos.CreateUserDto;
import com.IgorAlan.jobportal.models.dtos.UserDto;
import com.IgorAlan.jobportal.repository.UserRepository;
import com.IgorAlan.jobportal.repository.UserTypeRepository;

import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final UserTypeRepository userTypeRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;
    private final ProfileService profileService;

    public UserService(UserRepository userRepository,
            UserTypeRepository userTypeRepository,
            PasswordEncoder passwordEncoder,
            UserMapper userMapper,
            @Lazy ProfileService profileService) {
        this.userRepository = userRepository;
        this.userTypeRepository = userTypeRepository;
        this.passwordEncoder = passwordEncoder;
        this.userMapper = userMapper;
        this.profileService = profileService;
    }

    @Transactional
    public UserDto registerNewUser(CreateUserDto createUserDto) {
        userRepository.findByEmail(createUserDto.email()).ifPresent(user -> {
            throw new EmailAlreadyExistsException("Email " + createUserDto.email() + " já está em uso.");
        });

        User user = userMapper.toEntity(createUserDto);

        UserType userType = userTypeRepository.findById(createUserDto.userTypeId())
                .orElseThrow(() -> new RuntimeException("Tipo de usuário inválido: " + createUserDto.userTypeId()));
        user.setUserType(userType);

        user.setPassword(passwordEncoder.encode(createUserDto.password()));
        user.setActive(true);

        User savedUser = userRepository.save(user);

        profileService.createProfileForNewUser(savedUser);

        return userMapper.toDto(savedUser);
    }

    public User getCurrentAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = authentication.getName();
        return userRepository.findByEmail(currentUsername)
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado: " + currentUsername));
    }
}
