package com.example.backendapi.hours.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Request payload for logging billable hours.
 *
 * <p>Mirrors the monolith form fields from hours.jsp. If date is omitted, the backend defaults to
 * today.
 */
public record CreateBillableHourRequest(
        @NotNull(message = "customerId is required") Long customerId,
        @NotNull(message = "userId is required") Long userId,
        @NotNull(message = "categoryId is required") Long categoryId,
        @NotNull(message = "hours is required") @DecimalMin(value = "0.0", inclusive = false, message = "hours must be > 0")
                BigDecimal hours,
        String note,
        LocalDate date) {}
