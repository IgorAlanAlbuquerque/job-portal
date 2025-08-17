package com.igoralan.jobportal.services;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.igoralan.jobportal.exception.EmailAlreadyExistsException;
import com.igoralan.jobportal.mapper.UserMapper;
import com.igoralan.jobportal.models.User;
import com.igoralan.jobportal.models.UserType;
import com.igoralan.jobportal.models.dtos.CreateUserDto;
import com.igoralan.jobportal.repository.UserRepository;
import com.igoralan.jobportal.repository.UserTypeRepository;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserTypeRepository userTypeRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private UserMapper userMapper;

    @Mock
    private ProfileService profileService;

    @InjectMocks
    private UserService userService;

    @Test
    void registerNewUser_shouldSucceed_whenEmailIsNotTaken() {
        CreateUserDto dto = new CreateUserDto("Igor", "Alan", "test@email.com", "password123", 2L);
        User user = new User();
        user.setEmail(dto.email());
        UserType userType = new UserType();
        userType.setUserTypeId(2L);

        when(userRepository.findByEmail(dto.email())).thenReturn(Optional.empty());
        when(userTypeRepository.findById(dto.userTypeId())).thenReturn(Optional.of(userType));
        when(userMapper.toEntity(dto)).thenReturn(user);
        when(passwordEncoder.encode(dto.password())).thenReturn("hashedPassword");
        when(userRepository.save(any(User.class))).thenReturn(user);

        userService.registerNewUser(dto);

        verify(userRepository, times(1)).save(any(User.class));
        verify(profileService, times(1)).createProfileForNewUser(user);
        assertThat(user.getPassword()).isEqualTo("hashedPassword");
        assertThat(user.isActive()).isTrue();
    }

    @Test
    void registerNewUser_shouldThrowException_whenEmailIsTaken() {
        CreateUserDto dto = new CreateUserDto("Igor", "Alan", "test@email.com", "password123", 2L);
        when(userRepository.findByEmail(dto.email())).thenReturn(Optional.of(new User()));

        assertThrows(EmailAlreadyExistsException.class, () -> {
            userService.registerNewUser(dto);
        });

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void getCurrentAuthenticatedUser_shouldReturnUser_whenAuthenticated() {
        String userEmail = "logged.in@user.com";
        User expectedUser = new User();
        expectedUser.setEmail(userEmail);

        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        when(authentication.getName()).thenReturn(userEmail);

        when(userRepository.findByEmail(userEmail)).thenReturn(Optional.of(expectedUser));

        User actualUser = userService.getCurrentAuthenticatedUser();

        assertThat(actualUser).isNotNull();
        assertThat(actualUser.getEmail()).isEqualTo(userEmail);
    }

    @Test
    void getCurrentAuthenticatedUser_shouldThrowException_whenUserNotFound() {
        String userEmail = "non.existent@user.com";

        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        when(authentication.getName()).thenReturn(userEmail);

        when(userRepository.findByEmail(userEmail)).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () -> {
            userService.getCurrentAuthenticatedUser();
        });
    }

}
