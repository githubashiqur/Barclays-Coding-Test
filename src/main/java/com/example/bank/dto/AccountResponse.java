package com.example.bank.dto;
import java.math.BigDecimal;

public record AccountResponse(Long id, String accountNumber, BigDecimal balance, Long ownerId) {}