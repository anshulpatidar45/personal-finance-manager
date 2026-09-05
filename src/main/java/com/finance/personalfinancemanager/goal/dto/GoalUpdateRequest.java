package com.finance.personalfinancemanager.goal.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Future;

import java.math.BigDecimal;
import java.time.LocalDate;

/** Payload for updating a savings goal. Only target amount and date can be modified. */
public record GoalUpdateRequest(

        @DecimalMin(value = "0.01", message = "Target amount must be positive")
        BigDecimal targetAmount,

        @Future(message = "Target date must be a future date")
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
        LocalDate targetDate
) {}