package com.IgorAlan.jobportal.controller;

import com.IgorAlan.jobportal.models.dtos.CreateUserDto;
import com.IgorAlan.jobportal.models.dtos.UserDto;
import com.IgorAlan.jobportal.services.UserService;
import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
