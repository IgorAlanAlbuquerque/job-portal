package com.igoralan.jobportal.services;

import com.igoralan.jobportal.config.JwtProperties;
import io.jsonwebtoken.JwtException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class JwtServiceTest {

    @Mock
    private UserDetails userDetails;

    private JwtService jwtService;
    private final String testUserName = "user@test.com";

    @BeforeEach
    void setUp() {
        String testSecretKey = "YS12ZXJ5LXNlY3VyZS1zZWNyZXQta2V5LWZvci10ZXN0aW5n";
        long tenMinutesInMillis = 10 * 60 * 1000;
        JwtProperties jwtProperties = new JwtProperties(testSecretKey, tenMinutesInMillis);
        jwtService = new JwtService(jwtProperties);
    }

    @Test
    void generateToken_shouldCreateValidTokenForUser() {
        when(userDetails.getUsername()).thenReturn(testUserName);
        String token = jwtService.generateToken(userDetails);

        assertThat(token).isNotNull().isNotEmpty();
        String extractedUsername = jwtService.extractUsername(token);
        assertThat(extractedUsername).isEqualTo(testUserName);
    }

    @Test
    void extractUsername_shouldReturnCorrectUsernameFromToken() {
        when(userDetails.getUsername()).thenReturn(testUserName);
        String token = jwtService.generateToken(userDetails);
        String extractedUsername = jwtService.extractUsername(token);

        assertThat(extractedUsername).isEqualTo(testUserName);
    }

    @Test
    void isTokenValid_shouldReturnTrue_whenTokenIsValidAndUsernameMatches() {
        when(userDetails.getUsername()).thenReturn(testUserName);
        String token = jwtService.generateToken(userDetails);
        boolean isValid = jwtService.isTokenValid(token, userDetails);

        assertThat(isValid).isTrue();
    }

    @Test
    void isTokenValid_shouldReturnFalse_whenUsernameDoesNotMatch() {
        when(userDetails.getUsername()).thenReturn(testUserName);
        String token = jwtService.generateToken(userDetails);
        UserDetails otherUserDetails = mock(UserDetails.class);
        when(otherUserDetails.getUsername()).thenReturn("other-user@test.com");

        boolean isValid = jwtService.isTokenValid(token, otherUserDetails);

        assertThat(isValid).isFalse();
    }

    @Test
    void isTokenValid_shouldReturnFalse_whenTokenIsExpired() {
        String testSecretKey = "YS12ZXJ5LXNlY3VyZS1zZWNyZXQta2V5LWZvci10ZXN0aW5n";
        long negativeExpiration = -5000;
        JwtProperties expiredProperties = new JwtProperties(testSecretKey, negativeExpiration);
        JwtService expiredJwtService = new JwtService(expiredProperties);
        String expiredToken = expiredJwtService.generateToken(userDetails);

        boolean isValid = jwtService.isTokenValid(expiredToken, userDetails);

        assertThat(isValid).isFalse();
    }

    @Test
    void extractUsername_shouldThrowJwtException_whenTokenIsMalformed() {
        String malformedToken = "this.is.not.a.valid.token";

        assertThrows(JwtException.class, () -> {
            jwtService.extractUsername(malformedToken);
        });
    }
}
