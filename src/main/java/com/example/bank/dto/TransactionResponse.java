package com.example.bank.dto;

import com.example.bank.entity.TransactionType;
import java.math.BigDecimal;
import java.time.OffsetDateTime;

// DTO representing a transaction for an account.

public record TransactionResponse(
        Long id,
        Long accountId,
        TransactionType type,
        BigDecimal balance,
        BigDecimal amount,
        OffsetDateTime timestamp,
        String memo
) {}

