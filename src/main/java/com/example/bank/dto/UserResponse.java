package com.example.bank.dto;

// DTO returned for user profile information.

public record UserResponse(
        Long id,
        String username,
        String email
) {}

