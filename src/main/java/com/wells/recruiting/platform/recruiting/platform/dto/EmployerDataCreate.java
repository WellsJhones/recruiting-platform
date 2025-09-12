package com.wells.recruiting.platform.recruiting.platform.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record EmployerDataCreate(
        @NotBlank String name,
        @NotBlank @Email String email,
        @NotNull String password,
        @NotNull String role,
        String companyName,
        String companyDescription,
        String companyLogo
) {
}
