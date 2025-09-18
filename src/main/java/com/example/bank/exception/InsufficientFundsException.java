package com.example.bank.exception;

public class InsufficientFundsException extends RuntimeException {
    public InsufficientFundsException(String m) { super(m); }
}