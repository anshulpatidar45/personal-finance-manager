package com.finance.personalfinancemanager.goal.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDate;

/** Payload for creating a savings goal. */
public record GoalCreateRequest(

        @NotBlank(message = "Goal name is required")
        @Size(max = 150, message = "Goal name must be at most 150 characters")
        String goalName,

        @NotNull(message = "Target amount is required")
        @DecimalMin(value = "0.01", message = "Target amount must be positive")
        BigDecimal targetAmount,

        @NotNull(message = "Target date is required")
        @Future(message = "Target date must be a future date")
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
        LocalDate targetDate,

        @PastOrPresent(message = "Start date cannot be a future date")
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
        LocalDate startDate
) {}