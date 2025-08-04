package com.IgorAlan.jobportal.controller;

import com.IgorAlan.jobportal.models.User;
import com.IgorAlan.jobportal.models.UserType;
import com.IgorAlan.jobportal.services.UsersService;
import com.IgorAlan.jobportal.services.UsersTypeService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/users")
public class UsersController {
    private final UsersTypeService usersTypeService;
    private final UsersService usersService;

    public UsersController(UsersTypeService usersTypeService, UsersService usersService) {
        this.usersTypeService = usersTypeService;
        this.usersService = usersService;
    }

    @GetMapping("/register")
    public ResponseEntity<?> getRegisterPage() {
        List<UserType> usersTypes = usersTypeService.getAllUsersTypes();
        return ResponseEntity.ok(usersTypes);
    }

    @PostMapping("/register/new")
    public ResponseEntity<?> userRegister(@Valid @RequestBody User user) {

        Optional<User> optionalUsers = usersService.getUserByEmail(user.getEmail());

        if (optionalUsers.isPresent()) {
            return ResponseEntity.badRequest().body("Email already registered");
        }
        usersService.addNew(user);
        return ResponseEntity.status(201).body("User registered successfully");
    }

    @GetMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest request, HttpServletResponse response) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if(auth != null){
            new SecurityContextLogoutHandler().logout(request, response, auth);
        }
        return ResponseEntity.ok("Logged out successfully");
    }
}
