package com.example.backendapi.hours.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

/** A joined/enriched view of a billable hour entry suitable for recent listings and reports. */
public record BillableHourViewDto(
        Long id,
        LocalDate dateLogged,
        Long customerId,
        String customerName,
        Long userId,
        String userName,
        Long categoryId,
        String categoryName,
        BigDecimal hours,
        BigDecimal hourlyRate,
        BigDecimal lineTotal,
        String note,
        Instant createdAt) {}
