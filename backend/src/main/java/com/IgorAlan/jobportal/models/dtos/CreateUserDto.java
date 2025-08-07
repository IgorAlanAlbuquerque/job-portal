package com.IgorAlan.jobportal.models.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateUserDto(
        @NotBlank(message = "O primeiro nome não pode estar em branco") String firstName,

        @NotBlank(message = "O último nome não pode estar em branco") String lastName,

        @NotBlank(message = "O e-mail não pode estar em branco") @Email(message = "Formato de e-mail inválido") String email,

        @NotBlank(message = "A senha não pode estar em branco") @Size(min = 8, message = "A senha deve ter no mínimo 8 caracteres") String password,

        @NotNull(message = "O tipo de usuário é obrigatório") Long userTypeId) {
}
