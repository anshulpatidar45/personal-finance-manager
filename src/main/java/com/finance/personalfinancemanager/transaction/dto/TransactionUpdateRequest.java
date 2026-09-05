package com.finance.personalfinancemanager.transaction.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

/**
 * Payload for updating a transaction.
 * Note: The 'date' field is intentionally omitted. The assignment states
 * "Users can modify any transaction field except the date field."
 * If a client sends 'date' in the JSON, Spring Boot's default Jackson
 * configuration will simply ignore the unknown property.
 */
public record TransactionUpdateRequest(

        @DecimalMin(value = "0.01", message = "Amount must be positive")
        BigDecimal amount,

        String category,

        @Size(max = 500, message = "Description must be at most 500 characters")
        String description
) {}