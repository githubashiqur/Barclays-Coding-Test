package com.example.bank.dto;
import jakarta.validation.constraints.NotBlank;

public record AccountCreateRequest(@NotBlank String accountNumber) {}