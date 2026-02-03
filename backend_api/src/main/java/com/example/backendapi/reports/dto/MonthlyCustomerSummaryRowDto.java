package com.example.backendapi.reports.dto;

import java.math.BigDecimal;

/** One row in the monthly report grouped by customer. */
public record MonthlyCustomerSummaryRowDto(String customerName, BigDecimal totalHours, BigDecimal totalAmount) {}
