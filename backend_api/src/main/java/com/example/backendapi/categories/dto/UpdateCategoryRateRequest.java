package com.example.backendapi.categories.dto;

import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

/** Request payload for updating a billing category hourly rate. */
public record UpdateCategoryRateRequest(@NotNull(message = "newRate is required") BigDecimal newRate) {}
