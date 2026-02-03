package com.example.backendapi.reports.dto;

import java.math.BigDecimal;

/** Revenue summary row by billing category. */
public record RevenueByCategoryRowDto(
        Long categoryId,
        String categoryName,
        BigDecimal hourlyRate,
        BigDecimal totalHours,
        BigDecimal totalRevenue) {}
