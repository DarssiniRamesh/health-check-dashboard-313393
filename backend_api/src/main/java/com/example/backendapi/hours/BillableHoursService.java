package com.example.backendapi.hours;

import com.example.backendapi.customers.CustomerRepository;
import com.example.backendapi.error.NotFoundException;
import com.example.backendapi.hours.dto.BillableHourViewDto;
import com.example.backendapi.hours.dto.CreateBillableHourRequest;
import com.example.backendapi.users.UserRepository;
import com.example.backendapi.categories.BillingCategoryRepository;
import java.util.List;
import org.springframework.stereotype.Service;

/** Business logic for billable hours (mirrors BillableHourDAO and hours.jsp). */
@Service
public class BillableHoursService {

    private final BillableHourRepository billableHourRepository;
    private final CustomerRepository customerRepository;
    private final UserRepository userRepository;
    private final BillingCategoryRepository billingCategoryRepository;

    public BillableHoursService(
            BillableHourRepository billableHourRepository,
            CustomerRepository customerRepository,
            UserRepository userRepository,
            BillingCategoryRepository billingCategoryRepository) {
        this.billableHourRepository = billableHourRepository;
        this.customerRepository = customerRepository;
        this.userRepository = userRepository;
        this.billingCategoryRepository = billingCategoryRepository;
    }

    // PUBLIC_INTERFACE
    public BillableHourViewDto logHours(CreateBillableHourRequest request) {
        /**
         * Logs billable hours.
         *
         * <p>The monolith relies on form presence checks + DB constraints; here we explicitly verify
         * referenced IDs exist to provide a clean 404 error rather than a generic SQL exception.
         */
        customerRepository
                .findById(request.customerId())
                .orElseThrow(() -> new NotFoundException("Customer not found"));
        userRepository.findById(request.userId()).orElseThrow(() -> new NotFoundException("User not found"));
        billingCategoryRepository
                .findById(request.categoryId())
                .orElseThrow(() -> new NotFoundException("Billing category not found"));

        return billableHourRepository.insert(
                request.customerId(),
                request.userId(),
                request.categoryId(),
                request.hours(),
                request.note(),
                request.date());
    }

    // PUBLIC_INTERFACE
    public List<BillableHourViewDto> recentHours(int limit) {
        /** Returns recent logged hours (monolith shows up to 20). */
        return billableHourRepository.findRecent(limit);
    }
}
