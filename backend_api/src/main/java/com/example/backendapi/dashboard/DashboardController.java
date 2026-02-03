package com.example.backendapi.dashboard;

import com.example.backendapi.api.ApiConstants;
import com.example.backendapi.dashboard.dto.DashboardSummaryDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** Dashboard endpoints mirroring the legacy index.jsp summary metrics. */
@RestController
@RequestMapping(ApiConstants.API_BASE)
@Tag(name = ApiConstants.TAG_DASHBOARD)
public class DashboardController {

    private final DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    // PUBLIC_INTERFACE
    @GetMapping("/dashboard")
    @Operation(
            summary = "Dashboard summary metrics",
            description = "Returns total customers, total users, and total revenue (sum(hours * hourly_rate)).")
    public DashboardSummaryDto summary() {
        /** Returns dashboard summary values. */
        return dashboardService.getSummary();
    }
}
