package com.example.bank.dto;
import com.example.bank.entity.TransactionType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

// DTO to create a transaction against an account.

public record TransactionCreateRequest(
        @NotNull TransactionType type,
        @NotNull @DecimalMin("0.01") BigDecimal amount,
        String memo
) {}