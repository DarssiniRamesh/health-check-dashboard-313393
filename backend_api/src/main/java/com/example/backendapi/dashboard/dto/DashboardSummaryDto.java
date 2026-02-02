package com.example.backendapi.dashboard.dto;

import java.math.BigDecimal;

/** Dashboard summary values mirrored from the monolith's index.jsp calculations. */
public record DashboardSummaryDto(long totalCustomers, long totalUsers, BigDecimal totalRevenue) {}
