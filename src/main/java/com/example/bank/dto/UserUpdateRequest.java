package com.example.bank.dto;

// DTO for partial updates to a user's profile. Only for email and password.
public record UserUpdateRequest(
        String email,
        String password
) {}