package com.finance.personalfinancemanager.category.dto;

import com.finance.personalfinancemanager.category.CategoryType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/** Payload for creating a custom category. */
public record CategoryRequest(

        @NotBlank(message = "Category name is required")
        @Size(max = 100, message = "Category name must be at most 100 characters")
        String name,

        @NotNull(message = "Category type is required")
        CategoryType type
) {}