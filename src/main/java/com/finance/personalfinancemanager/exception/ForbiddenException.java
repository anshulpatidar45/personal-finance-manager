package com.finance.personalfinancemanager.exception;

/** Authenticated user attempted to access another user's data. Mapped to HTTP 403. */
public class ForbiddenException extends RuntimeException {
    public ForbiddenException(String message) { super(message); }
}