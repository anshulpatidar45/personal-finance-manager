package com.finance.personalfinancemanager.exception;

/** Missing or invalid authentication. Mapped to HTTP 401. */
public class UnauthorizedException extends RuntimeException {
    public UnauthorizedException(String message) { super(message); }
}