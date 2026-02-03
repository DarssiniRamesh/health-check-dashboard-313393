package com.example.backendapi.reports.dto;

import java.util.List;

/** Revenue summary response including breakdown by customer and by category. */
public record RevenueSummaryDto(
        List<RevenueByCustomerRowDto> byCustomer, List<RevenueByCategoryRowDto> byCategory) {}
