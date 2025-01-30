package com.IgorAlan.jobportal.controller;

import com.IgorAlan.jobportal.entity.Users;
import com.IgorAlan.jobportal.entity.UsersType;
import com.IgorAlan.jobportal.services.UsersService;
import com.IgorAlan.jobportal.services.UsersTypeService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;
import java.util.Optional;

@Controller
public class UsersController {
    private final UsersTypeService usersTypeService;
    private final UsersService usersService;

    @Autowired
    public UsersController(UsersTypeService usersTypeService, UsersService usersService) {
        this.usersTypeService = usersTypeService;
        this.usersService = usersService;
    }

    @GetMapping("/register")
    public String register(Model model) {
        List<UsersType> usersTypes = usersTypeService.getAllUsersTypes();
        model.addAttribute("getAllTypes", usersTypes);
        model.addAttribute("user", new Users());
        return "register";
    }

    @PostMapping("/register/new")
    public String userRegister(@Valid Users user, Model model) {

        Optional<Users> optionalUsers = usersService.getUserByEmail(user.getEmail());

        if (optionalUsers.isPresent()) {
            model.addAttribute("error", "Email already registered");
            List<UsersType> usersTypes = usersTypeService.getAllUsersTypes();
            model.addAttribute("getAllTypes", usersTypes);
            model.addAttribute("user", new Users());
            return "register";

        }
        usersService.addNew(user);
        return "dashboard";
    }
}
