package com.finance.personalfinancemanager.exception;

/** Duplicate resource (username, category name...). Mapped to HTTP 409. */
public class ConflictException extends RuntimeException {
    public ConflictException(String message) { super(message); }
}