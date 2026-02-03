package com.example.backendapi.reports;

import com.example.backendapi.customers.CustomerRepository;
import com.example.backendapi.error.NotFoundException;
import com.example.backendapi.reports.dto.CustomerBillLineItemDto;
import com.example.backendapi.reports.dto.CustomerBillReportDto;
import com.example.backendapi.reports.dto.MonthlyReportDto;
import com.example.backendapi.reports.dto.RevenueSummaryDto;
import java.math.BigDecimal;
import java.util.List;
import org.springframework.stereotype.Service;

/** Business logic for reports (mirrors reports.jsp behavior). */
@Service
public class ReportsService {

    private final ReportsRepository reportsRepository;
    private final CustomerRepository customerRepository;

    public ReportsService(ReportsRepository reportsRepository, CustomerRepository customerRepository) {
        this.reportsRepository = reportsRepository;
        this.customerRepository = customerRepository;
    }

    // PUBLIC_INTERFACE
    public CustomerBillReportDto customerBill(long customerId) {
        /** Customer bill report: line items + totals. */
        var customer =
                customerRepository
                        .findById(customerId)
                        .orElseThrow(() -> new NotFoundException("Customer not found"));

        List<CustomerBillLineItemDto> items = reportsRepository.customerBillLineItems(customerId);

        BigDecimal totalHours =
                items.stream()
                        .map(CustomerBillLineItemDto::hours)
                        .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalAmount =
                items.stream()
                        .map(CustomerBillLineItemDto::lineTotal)
                        .reduce(BigDecimal.ZERO, BigDecimal::add);

        return new CustomerBillReportDto(customerId, customer.name(), items, totalHours, totalAmount);
    }

    // PUBLIC_INTERFACE
    public MonthlyReportDto monthly(int year, int month) {
        /** Monthly report grouped by customer for year/month. */
        return new MonthlyReportDto(year, month, reportsRepository.monthlySummary(year, month));
    }

    // PUBLIC_INTERFACE
    public RevenueSummaryDto revenueSummary() {
        /** Revenue summary by customer and by category. */
        return new RevenueSummaryDto(reportsRepository.revenueByCustomer(), reportsRepository.revenueByCategory());
    }
}
