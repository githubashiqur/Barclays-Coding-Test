package com.example.bank.dto;

import java.time.OffsetDateTime;

public record ErrorResponse(
        String error,
        String message,
        int status,
        OffsetDateTime timestamp
) {}