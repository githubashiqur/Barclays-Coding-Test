package com.example.bank.dto;

public record UserResponse(
        Long id,
        String username,
        String email
) {}

