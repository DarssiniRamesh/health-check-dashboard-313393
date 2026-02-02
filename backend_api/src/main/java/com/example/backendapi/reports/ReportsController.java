package com.example.backendapi.reports;

import com.example.backendapi.api.ApiConstants;
import com.example.backendapi.reports.dto.CustomerBillReportDto;
import com.example.backendapi.reports.dto.MonthlyReportDto;
import com.example.backendapi.reports.dto.RevenueSummaryDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/** Reporting endpoints (mirrors reports.jsp report types). */
@RestController
@RequestMapping(ApiConstants.API_BASE + "/reports")
@Validated
@Tag(name = ApiConstants.TAG_REPORTS)
public class ReportsController {

    private final ReportsService reportsService;

    public ReportsController(ReportsService reportsService) {
        this.reportsService = reportsService;
    }

    // PUBLIC_INTERFACE
    @GetMapping("/customer/{customerId}")
    @Operation(summary = "Customer bill report", description = "Generates a customer bill report (line items + totals).")
    public CustomerBillReportDto customer(@PathVariable("customerId") long customerId) {
        /** Customer bill report. */
        return reportsService.customerBill(customerId);
    }

    // PUBLIC_INTERFACE
    @GetMapping("/monthly")
    @Operation(summary = "Monthly report", description = "Generates monthly summary grouped by customer for a year/month.")
    public MonthlyReportDto monthly(
            @RequestParam("year") @Min(1970) @Max(2100) int year,
            @RequestParam("month") @Min(1) @Max(12) int month) {
        /** Monthly report. */
        return reportsService.monthly(year, month);
    }

    // PUBLIC_INTERFACE
    @GetMapping("/revenue")
    @Operation(
            summary = "Revenue summary report",
            description = "Returns revenue summary by customer and by category.")
    public RevenueSummaryDto revenue() {
        /** Revenue summary report. */
        return reportsService.revenueSummary();
    }
}
