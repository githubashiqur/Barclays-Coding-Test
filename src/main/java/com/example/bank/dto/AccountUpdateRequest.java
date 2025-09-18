package com.example.bank.dto;

// DTO for partial account updates. Currently only supports updating the account number.

public record AccountUpdateRequest(String accountNumber) {}
