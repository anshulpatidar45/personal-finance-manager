package com.finance.personalfinancemanager.category.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.finance.personalfinancemanager.category.CategoryType;

public record CategoryResponse(
        Long id,
        String name,
        CategoryType type,
        @JsonProperty("isCustom") boolean isCustom,
        @JsonProperty("custom") boolean custom
) {
    public CategoryResponse(Long id, String name, CategoryType type, boolean isCustom) {
        this(id, name, type, isCustom, isCustom);
    }
}