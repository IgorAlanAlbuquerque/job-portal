package com.IgorAlan.jobportal.services;

import com.IgorAlan.jobportal.models.UserType;
import com.IgorAlan.jobportal.repository.UserTypeRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UsersTypeService {

    private final UserTypeRepository usersTypeRepository;

    public UsersTypeService(UserTypeRepository usersTypeRepository) {
        this.usersTypeRepository = usersTypeRepository;
    }

    public List<UserType> getAllUsersTypes() {
        return usersTypeRepository.findAll();
    }
}
