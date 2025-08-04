package com.IgorAlan.jobportal.mapper;

import com.IgorAlan.jobportal.elasticsearch.document.JobPostDocument;
import com.IgorAlan.jobportal.models.JobPostActivity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface JobPostMapper {
    @Mapping(source = "jobCompany.name", target = "companyName")
    @Mapping(source = "jobLocation.city", target = "city")
    @Mapping(source = "jobLocation.state", target = "state")
    JobPostDocument toDocument(JobPostActivity entity);
}