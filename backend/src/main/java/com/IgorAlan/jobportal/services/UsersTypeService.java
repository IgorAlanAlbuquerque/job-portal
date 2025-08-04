package com.IgorAlan.jobportal.services;

import com.IgorAlan.jobportal.models.UserType;
import com.IgorAlan.jobportal.repository.UsersTypeRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UsersTypeService {

    private final UsersTypeRepository usersTypeRepository;

    public UsersTypeService(UsersTypeRepository usersTypeRepository) {
        this.usersTypeRepository = usersTypeRepository;
    }

    public List<UserType> getAllUsersTypes() {
        return usersTypeRepository.findAll();
    }
}
