package com.IgorAlan.jobportal.mapper;

import com.IgorAlan.jobportal.elasticsearch.document.JobDocument;
import com.IgorAlan.jobportal.models.Job;
import com.IgorAlan.jobportal.models.JobSeekerApply;
import com.IgorAlan.jobportal.models.dtos.ApplicantSummaryDto;
import com.IgorAlan.jobportal.models.dtos.CreateJobDto;
import com.IgorAlan.jobportal.models.dtos.JobDetailDto;
import com.IgorAlan.jobportal.models.dtos.JobSummaryDto;
import com.IgorAlan.jobportal.models.dtos.UpdateJobDto;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring")
public interface JobMapper {

    @Mapping(source = "jobPostId", target = "id")
    @Mapping(source = "descriptionOfJob", target = "description")
    @Mapping(source = "jobCompany.name", target = "companyName")
    @Mapping(source = "jobLocation.city", target = "city")
    @Mapping(source = "jobLocation.state", target = "state")
    JobDocument toDocument(Job entity);

    @Mapping(source = "jobCompany.name", target = "companyName")
    @Mapping(source = "jobLocation.city", target = "city")
    @Mapping(source = "jobLocation.state", target = "state")
    JobSummaryDto toSummaryDto(Job entity);

    @Mapping(source = "id", target = "jobPostId")
    JobSummaryDto toSummaryDto(JobDocument document);

    @Mapping(source = "entity.jobPostId", target = "jobPostId")
    @Mapping(source = "entity.jobTitle", target = "jobTitle")
    @Mapping(source = "entity.descriptionOfJob", target = "description")
    @Mapping(source = "entity.jobCompany.name", target = "companyName")
    @Mapping(source = "entity.jobCompany.logo", target = "companyLogoUrl")
    @Mapping(source = "entity.jobLocation.city", target = "city")
    @Mapping(source = "entity.jobLocation.state", target = "state")
    @Mapping(source = "entity.jobLocation.country", target = "country")
    @Mapping(source = "entity.jobType", target = "jobType")
    @Mapping(source = "entity.salary", target = "salary")
    @Mapping(source = "entity.remote", target = "remote")
    @Mapping(source = "entity.postedDate", target = "postedDate")
    @Mapping(source = "entity.postedBy.firstName", target = "recruiterName")
    @Mapping(source = "isSaved", target = "isSaved")
    @Mapping(source = "isApplied", target = "isApplied")
    JobDetailDto toDetailDto(Job entity, boolean isSaved, boolean isApplied);

    @Mapping(target = "jobPostId", ignore = true)
    @Mapping(target = "postedBy", ignore = true)
    @Mapping(target = "jobLocation", ignore = true)
    @Mapping(target = "jobCompany", ignore = true)
    @Mapping(target = "active", ignore = true)
    @Mapping(target = "postedDate", ignore = true)
    Job toEntity(CreateJobDto dto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(source = "locationId", target = "jobLocation.id")
    @Mapping(source = "companyId", target = "jobCompany.id")
    @Mapping(target = "jobPostId", ignore = true)
    @Mapping(target = "postedBy", ignore = true)
    @Mapping(target = "postedDate", ignore = true)
    @Mapping(target = "active", ignore = true)
    void updateEntityFromDto(UpdateJobDto dto, @MappingTarget Job job);

    @Mapping(source = "profile.userAccountId", target = "userAccountId")
    @Mapping(source = "profile.user.firstName", target = "firstName")
    @Mapping(source = "profile.user.lastName", target = "lastName")
    @Mapping(source = "profile.profilePhoto", target = "profilePhotoUrl")
    ApplicantSummaryDto toApplicantSummaryDto(JobSeekerApply apply);
}