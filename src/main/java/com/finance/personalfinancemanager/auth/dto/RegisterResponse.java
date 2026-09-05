package com.finance.personalfinancemanager.auth.dto;

/** Registration success payload, matching the assignment's API contract. */
public record RegisterResponse(String message, Long userId) {}