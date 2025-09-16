package com.example.bank.dto;

public record UserUpdateRequest(
        String email,
        String password
) {}