package com.finance.personalfinancemanager.exception;

// Invalid or malformed client input. Mapped to HTTP 400.
public class BadRequestException extends RuntimeException {
    public BadRequestException(String message) { super(message); }
}