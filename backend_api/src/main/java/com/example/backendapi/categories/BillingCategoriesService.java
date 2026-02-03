package com.example.backendapi.categories;

import com.example.backendapi.categories.dto.BillingCategoryDto;
import com.example.backendapi.categories.dto.CreateBillingCategoryRequest;
import com.example.backendapi.categories.dto.UpdateCategoryRateRequest;
import com.example.backendapi.error.NotFoundException;
import java.util.List;
import org.springframework.stereotype.Service;

/** Business logic for billing categories. */
@Service
public class BillingCategoriesService {

    private final BillingCategoryRepository categoryRepository;

    public BillingCategoriesService(BillingCategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    // PUBLIC_INTERFACE
    public List<BillingCategoryDto> listCategories() {
        /** Lists categories including aggregates (hours, revenue) as shown in the monolith UI. */
        return categoryRepository.findAllWithAggregates();
    }

    // PUBLIC_INTERFACE
    public BillingCategoryDto createCategory(CreateBillingCategoryRequest request) {
        /** Creates a new billing category. */
        return categoryRepository.insert(request.name(), request.description(), request.hourlyRate());
    }

    // PUBLIC_INTERFACE
    public BillingCategoryDto updateRate(long id, UpdateCategoryRateRequest request) {
        /** Updates category hourly rate (mirrors action=update in categories.jsp). */
        return categoryRepository
                .updateRate(id, request.newRate())
                .orElseThrow(() -> new NotFoundException("Billing category not found"));
    }
}
