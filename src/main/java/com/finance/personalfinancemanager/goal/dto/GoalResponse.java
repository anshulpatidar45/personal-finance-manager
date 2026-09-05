package com.finance.personalfinancemanager.goal.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Goal view model including dynamically calculated progress fields.
 * Progress is computed on read and never persisted.
 */
public record GoalResponse(
        Long id,
        String goalName,
        BigDecimal targetAmount,
        LocalDate targetDate,
        LocalDate startDate,
        BigDecimal currentProgress,
        Double progressPercentage,
        BigDecimal remainingAmount
) {}