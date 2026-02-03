package com.example.backendapi.categories.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

/** Request payload for creating a billing category. */
public record CreateBillingCategoryRequest(
        @NotBlank(message = "name is required") String name,
        String description,
        @NotNull(message = "hourlyRate is required") BigDecimal hourlyRate) {}
