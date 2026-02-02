package com.example.backendapi.categories;

import com.example.backendapi.api.ApiConstants;
import com.example.backendapi.categories.dto.BillingCategoryDto;
import com.example.backendapi.categories.dto.CreateBillingCategoryRequest;
import com.example.backendapi.categories.dto.UpdateCategoryRateRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

/** Billing category endpoints (mirrors categories.jsp and BillingCategoryDAO). */
@RestController
@RequestMapping(ApiConstants.API_BASE + "/categories")
@Tag(name = ApiConstants.TAG_CATEGORIES)
public class BillingCategoriesController {

    private final BillingCategoriesService categoriesService;

    public BillingCategoriesController(BillingCategoriesService categoriesService) {
        this.categoriesService = categoriesService;
    }

    // PUBLIC_INTERFACE
    @GetMapping
    @Operation(
            summary = "List billing categories",
            description = "Returns billing categories including aggregate totals (hours and revenue).")
    public List<BillingCategoryDto> list() {
        /** Lists categories with aggregates. */
        return categoriesService.listCategories();
    }

    // PUBLIC_INTERFACE
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create billing category", description = "Creates a billing category.")
    public BillingCategoryDto create(@Valid @RequestBody CreateBillingCategoryRequest request) {
        /** Creates a billing category. */
        return categoriesService.createCategory(request);
    }

    // PUBLIC_INTERFACE
    @PatchMapping("/{id}/rate")
    @Operation(summary = "Update hourly rate", description = "Updates hourly_rate for a billing category.")
    public BillingCategoryDto updateRate(
            @PathVariable("id") long id, @Valid @RequestBody UpdateCategoryRateRequest request) {
        /** Updates hourly rate. */
        return categoriesService.updateRate(id, request);
    }
}
