package com.IgorAlan.jobportal.models.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateJobDto(
    @NotBlank(message = "O título da vaga é obrigatório")
    String jobTitle,

    @NotBlank(message = "A descrição é obrigatória")
    @Size(max = 10000, message = "A descrição é muito longa")
    String descriptionOfJob,

    @NotNull(message = "O ID da localização é obrigatório")
    Long locationId,

    @NotNull(message = "O ID da empresa é obrigatório")
    Long companyId,

    @NotBlank(message = "O tipo de trabalho é obrigatório")
    String jobType,

    String salary,
    String remote
) {}
