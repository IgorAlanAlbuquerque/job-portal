package com.IgorAlan.jobportal.services;

import com.IgorAlan.jobportal.models.dtos.AuthRequestDto;
import com.IgorAlan.jobportal.models.dtos.AuthResponseDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private UserDetailsService userDetailsService;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private AuthService authService;

    @Test
    void login_shouldReturnAuthResponseDto_whenCredentialsAreValid() {
        AuthRequestDto request = new AuthRequestDto("user@test.com", "password");
        UserDetails userDetails = new User("user@test.com", "password", new ArrayList<>());
        String expectedToken = "generated-jwt-token";

        when(userDetailsService.loadUserByUsername(request.email())).thenReturn(userDetails);
        when(jwtService.generateToken(userDetails)).thenReturn(expectedToken);

        AuthResponseDto response = authService.login(request);

        verify(authenticationManager, times(1)).authenticate(
                new UsernamePasswordAuthenticationToken(request.email(), request.password()));
        verify(jwtService, times(1)).generateToken(userDetails);

        assertThat(response).isNotNull();
        assertThat(response.token()).isEqualTo(expectedToken);
    }
}
