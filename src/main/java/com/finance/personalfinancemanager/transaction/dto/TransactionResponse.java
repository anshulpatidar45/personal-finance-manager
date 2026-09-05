package com.finance.personalfinancemanager.transaction.dto;

import com.finance.personalfinancemanager.category.CategoryType;

import java.math.BigDecimal;
import java.time.LocalDate;

/** Transaction view model; type is derived from the category. */
public record TransactionResponse(
        Long id,
        BigDecimal amount,
        LocalDate date,
        String category,
        String description,
        CategoryType type
) {}