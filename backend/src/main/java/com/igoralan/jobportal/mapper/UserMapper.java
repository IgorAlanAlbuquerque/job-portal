package com.igoralan.jobportal.mapper;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import com.igoralan.jobportal.models.JobSeekerProfile;
import com.igoralan.jobportal.models.RecruiterProfile;
import com.igoralan.jobportal.models.User;
import com.igoralan.jobportal.models.dtos.*;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "password", ignore = true)
    @Mapping(target = "userType", ignore = true)
    @Mapping(target = "city", ignore = true)
    @Mapping(target = "state", ignore = true)
    @Mapping(target = "country", ignore = true)
    @Mapping(target = "active", ignore = true)
    @Mapping(target = "userId", ignore = true)
    @Mapping(target = "registrationDate", ignore = true)
    User toEntity(CreateUserDto dto);

    @Mapping(source = "userType.userTypeName", target = "userType")
    UserDto toDto(User user);

    @Mapping(source = "user.userId", target = "userId")
    @Mapping(source = "user.email", target = "email")
    @Mapping(source = "user.firstName", target = "firstName")
    @Mapping(source = "user.lastName", target = "lastName")
    @Mapping(source = "user.city", target = "city")
    @Mapping(source = "user.state", target = "state")
    @Mapping(source = "profilePhoto", target = "profilePhotoUrl")
    @Mapping(source = "user.userType.userTypeName", target = "userType")
    UserProfileDto toUserProfileDto(RecruiterProfile profile);

    @Mapping(source = "user.userId", target = "userId")
    @Mapping(source = "user.email", target = "email")
    @Mapping(source = "user.firstName", target = "firstName")
    @Mapping(source = "user.lastName", target = "lastName")
    @Mapping(source = "user.city", target = "city")
    @Mapping(source = "user.state", target = "state")
    @Mapping(source = "profilePhoto", target = "profilePhotoUrl")
    @Mapping(source = "user.userType.userTypeName", target = "userType")
    UserProfileDto toUserProfileDto(JobSeekerProfile profile);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(source = "firstName", target = "user.firstName")
    @Mapping(source = "lastName", target = "user.lastName")
    @Mapping(source = "city", target = "user.city")
    @Mapping(source = "state", target = "user.state")
    @Mapping(source = "country", target = "user.country")
    @Mapping(target = "userAccountId", ignore = true)
    void updateRecruiterProfileFromDto(UpdateRecruiterProfileDto dto, @MappingTarget RecruiterProfile profile);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "userAccountId", ignore = true)
    @Mapping(target = "skills", ignore = true)
    @Mapping(source = "firstName", target = "user.firstName")
    @Mapping(source = "lastName", target = "user.lastName")
    @Mapping(source = "city", target = "user.city")
    @Mapping(source = "state", target = "user.state")
    @Mapping(source = "country", target = "user.country")
    void updateJobSeekerProfileFromDto(UpdateJobSeekerProfileDto dto, @MappingTarget JobSeekerProfile profile);
}
