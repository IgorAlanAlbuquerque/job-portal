package com.igoralan.jobportal.controller;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.igoralan.jobportal.models.dtos.CreateUserDto;
import com.igoralan.jobportal.models.dtos.UserDto;
import com.igoralan.jobportal.services.UserService;

@RestController
@RequestMapping("/api/users")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public ResponseEntity<UserDto> registerUser(@Valid @RequestBody CreateUserDto createUserDto) {
        UserDto newUser = userService.registerNewUser(createUserDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(newUser);
    }
}
