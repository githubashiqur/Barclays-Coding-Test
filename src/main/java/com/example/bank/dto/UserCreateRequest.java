package com.example.bank.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

// DTO used to register a new user.

public record UserCreateRequest(
        @NotBlank String username,
        @NotBlank String password,
        @Email @NotBlank String email
) {}