package com.example.bank.dto;

import com.example.bank.entity.TransactionType;
import java.math.BigDecimal;
import java.time.OffsetDateTime;

public record TransactionResponse(
        Long id,
        Long accountId,
        TransactionType type,
        BigDecimal amount,
        OffsetDateTime timestamp,
        String memo
) {}

