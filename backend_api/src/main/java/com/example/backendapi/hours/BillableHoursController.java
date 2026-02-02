package com.example.backendapi.hours;

import com.example.backendapi.api.ApiConstants;
import com.example.backendapi.hours.dto.BillableHourViewDto;
import com.example.backendapi.hours.dto.CreateBillableHourRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

/** Billable hours endpoints (mirrors hours.jsp and BillableHourDAO). */
@RestController
@RequestMapping(ApiConstants.API_BASE + "/hours")
@Tag(name = ApiConstants.TAG_HOURS)
public class BillableHoursController {

    private final BillableHoursService billableHoursService;

    public BillableHoursController(BillableHoursService billableHoursService) {
        this.billableHoursService = billableHoursService;
    }

    // PUBLIC_INTERFACE
    @GetMapping
    @Operation(
            summary = "List recent billable hours",
            description = "Returns recent billable hour entries (default limit 20).")
    public List<BillableHourViewDto> recent(@RequestParam(value = "limit", defaultValue = "20") int limit) {
        /** Lists recent billable hour entries. */
        return billableHoursService.recentHours(limit);
    }

    // PUBLIC_INTERFACE
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Log billable hours", description = "Logs a new billable hour entry.")
    public BillableHourViewDto log(@Valid @RequestBody CreateBillableHourRequest request) {
        /** Logs a billable hour entry. */
        return billableHoursService.logHours(request);
    }
}
