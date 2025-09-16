package com.example.bank.exception;

public class ConflictException extends RuntimeException {
    public ConflictException(String m) { super(m); }
}