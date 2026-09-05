package com.finance.personalfinancemanager.category.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.finance.personalfinancemanager.category.CategoryType;

/** Category view model matching the assignment contract. */
public record CategoryResponse(
        Long id,
        String name,
        CategoryType type,
        @JsonProperty("isCustom") boolean isCustom
) {}