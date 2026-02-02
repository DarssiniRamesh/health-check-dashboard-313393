package com.example.backendapi.reports.dto;

import java.math.BigDecimal;
import java.util.List;

/** Customer bill report including line items and totals. */
public record CustomerBillReportDto(
        Long customerId,
        String customerName,
        List<CustomerBillLineItemDto> lineItems,
        BigDecimal totalHours,
        BigDecimal totalAmount) {}
