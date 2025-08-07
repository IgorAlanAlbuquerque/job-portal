package com.IgorAlan.jobportal.models.dtos;

import jakarta.validation.constraints.Size;

public record UpdateJobDto(
        @Size(min = 1, message = "O título da vaga não pode estar em branco") String jobTitle,

        @Size(max = 10000, message = "A descrição é muito longa") String descriptionOfJob,

        Long locationId,

        Long companyId,

        String jobType,

        String salary,

        String remote) {
}
