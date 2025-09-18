package com.example.bank.dto;

// Response returned after successful authentication. Contains a JWT token string.

public record AuthResponse(String token) {}
