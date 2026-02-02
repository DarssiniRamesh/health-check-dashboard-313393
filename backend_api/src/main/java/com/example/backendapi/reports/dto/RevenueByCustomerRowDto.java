package com.example.backendapi.reports.dto;

import java.math.BigDecimal;

/** Revenue summary row by customer. */
public record RevenueByCustomerRowDto(
        Long customerId, String customerName, BigDecimal totalHours, BigDecimal totalRevenue, BigDecimal avgRate) {}
