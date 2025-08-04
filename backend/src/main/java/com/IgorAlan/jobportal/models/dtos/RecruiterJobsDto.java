package com.IgorAlan.jobportal.models.dtos;

import com.IgorAlan.jobportal.models.JobCompany;
import com.IgorAlan.jobportal.models.JobLocation;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class RecruiterJobsDto {

    private Long totalCandidates;
    private Long jobPostId;
    private String jobTitle;
    private JobLocation jobLocationId;
    private JobCompany jobCompanyId;
}
