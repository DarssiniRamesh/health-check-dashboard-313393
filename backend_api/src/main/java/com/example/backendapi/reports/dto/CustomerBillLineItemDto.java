package com.example.backendapi.reports.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

/** Customer bill line item (mirrors the report table rows from reports.jsp). */
public record CustomerBillLineItemDto(
        LocalDate dateLogged,
        String userName,
        String categoryName,
        BigDecimal hours,
        BigDecimal hourlyRate,
        BigDecimal lineTotal,
        String note) {}
