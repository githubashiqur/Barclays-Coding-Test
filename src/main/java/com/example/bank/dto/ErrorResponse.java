package com.example.bank.dto;

import java.time.OffsetDateTime;

// Standard error payload returned by the API when exceptions occur.
public record ErrorResponse(
        String error,
        String message,
        int status,
        OffsetDateTime timestamp
) {}