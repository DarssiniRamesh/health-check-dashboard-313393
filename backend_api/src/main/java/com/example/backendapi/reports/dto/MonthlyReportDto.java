package com.example.backendapi.reports.dto;

import java.util.List;

/** Monthly report response for a given year/month. */
public record MonthlyReportDto(int year, int month, List<MonthlyCustomerSummaryRowDto> rows) {}
