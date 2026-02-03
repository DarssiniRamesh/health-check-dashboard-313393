package com.example.backendapi.categories.dto;

import java.math.BigDecimal;

/**
 * Billing category DTO.
 *
 * <p>Includes aggregate fields used by the legacy categories page: totalHours and totalRevenue.
 */
public record BillingCategoryDto(
        Long id,
        String name,
        String description,
        BigDecimal hourlyRate,
        BigDecimal totalHours,
        BigDecimal totalRevenue) {}
