package com.IgorAlan.jobportal.services;

import com.IgorAlan.jobportal.models.User;
import com.IgorAlan.jobportal.models.UserType;
import com.IgorAlan.jobportal.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CustomUserDetailsServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CustomUserDetailsService customUserDetailsService;

    @Test
    void loadUserByUsername_shouldReturnUserDetails_whenUserExists() {
        String userEmail = "recruiter@test.com";
        User user = new User();
        user.setEmail(userEmail);
        user.setPassword("hashedPassword");

        UserType recruiterType = new UserType();
        recruiterType.setUserTypeName("Recruiter");
        user.setUserType(recruiterType);

        when(userRepository.findByEmail(userEmail)).thenReturn(Optional.of(user));

        UserDetails userDetails = customUserDetailsService.loadUserByUsername(userEmail);

        assertThat(userDetails).isNotNull();
        assertThat(userDetails.getUsername()).isEqualTo(userEmail);
        assertThat(userDetails.getPassword()).isEqualTo("hashedPassword");
        assertThat(userDetails.getAuthorities()).hasSize(1);
        assertThat(userDetails.getAuthorities().iterator().next().getAuthority()).isEqualTo("Recruiter");
    }

    @Test
    void loadUserByUsername_shouldThrowException_whenUserDoesNotExist() {
        String userEmail = "non.existent@user.com";

        when(userRepository.findByEmail(userEmail)).thenReturn(Optional.empty());

        UsernameNotFoundException exception = assertThrows(UsernameNotFoundException.class, () -> {
            customUserDetailsService.loadUserByUsername(userEmail);
        });

        assertThat(exception.getMessage()).contains(userEmail);
    }
}
