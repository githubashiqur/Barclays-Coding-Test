package com.example.bank.dto;

import jakarta.validation.constraints.NotBlank;

// Authentication request payload. Contains username and password used by `/v1/auth/login`.
public record AuthRequest(
        @NotBlank String username,
        @NotBlank String password
) {}