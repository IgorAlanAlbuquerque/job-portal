package com.igoralan.jobportal.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.igoralan.jobportal.models.User;
import com.igoralan.jobportal.models.UserType;
import com.igoralan.jobportal.models.dtos.CreateUserDto;
import com.igoralan.jobportal.repository.UserRepository;
import com.igoralan.jobportal.repository.UserTypeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
@ActiveProfiles("test")
@Transactional
class UserControllerIT {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15-alpine");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserTypeRepository userTypeRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    private UserType jobSeekerType;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
        userTypeRepository.deleteAll();

        jobSeekerType = new UserType();
        jobSeekerType.setUserTypeName("Job Seeker");
        userTypeRepository.save(jobSeekerType);
    }

    @Test
    void registerUser_withValidDto_shouldReturnCreatedAndUserDto() throws Exception {
        CreateUserDto createUserDto = new CreateUserDto(
                "New",
                "User",
                "newuser@test.com",
                "password123",
                jobSeekerType.getUserTypeId());

        mockMvc.perform(post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createUserDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.userId").value(notNullValue()))
                .andExpect(jsonPath("$.email").value("newuser@test.com"))
                .andExpect(jsonPath("$.userType").value("Job Seeker"));

        assertThat(userRepository.findByEmail("newuser@test.com")).isPresent();
    }

    @Test
    void registerUser_whenEmailAlreadyExists_shouldReturnConflict() throws Exception {
        User existingUser = new User();
        existingUser.setEmail("existing@test.com");
        existingUser.setPassword(passwordEncoder.encode("password123"));
        existingUser.setUserType(jobSeekerType);
        existingUser.setActive(true);
        userRepository.save(existingUser);

        CreateUserDto createUserDto = new CreateUserDto(
                "Another",
                "User",
                "existing@test.com",
                "password456",
                jobSeekerType.getUserTypeId());

        mockMvc.perform(post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createUserDto)))
                .andExpect(status().isConflict());
    }

    @Test
    void registerUser_withInvalidDto_shouldReturnBadRequest() throws Exception {
        CreateUserDto createUserDto = new CreateUserDto(
                "New",
                "User",
                "",
                "password123",
                jobSeekerType.getUserTypeId());

        mockMvc.perform(post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createUserDto)))
                .andExpect(status().isBadRequest());
    }
}