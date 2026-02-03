package com.example.backendapi.dashboard;

import com.example.backendapi.customers.CustomerRepository;
import com.example.backendapi.dashboard.dto.DashboardSummaryDto;
import com.example.backendapi.reports.ReportsRepository;
import com.example.backendapi.users.UserRepository;
import java.math.BigDecimal;
import org.springframework.stereotype.Service;

/** Dashboard summary calculations (mirrors monolith index.jsp). */
@Service
public class DashboardService {

    private final CustomerRepository customerRepository;
    private final UserRepository userRepository;
    private final ReportsRepository reportsRepository;

    public DashboardService(
            CustomerRepository customerRepository, UserRepository userRepository, ReportsRepository reportsRepository) {
        this.customerRepository = customerRepository;
        this.userRepository = userRepository;
        this.reportsRepository = reportsRepository;
    }

    // PUBLIC_INTERFACE
    public DashboardSummaryDto getSummary() {
        /** Returns total customers, total users, and total revenue (sum(hours * rate)). */
        long totalCustomers = customerRepository.count();
        long totalUsers = userRepository.count();
        BigDecimal totalRevenue = reportsRepository.totalRevenue();
        return new DashboardSummaryDto(totalCustomers, totalUsers, totalRevenue);
    }
}
