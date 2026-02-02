package com.example.backendapi.bootstrap;

import com.example.backendapi.categories.BillingCategoryRepository;
import com.example.backendapi.customers.CustomerRepository;
import com.example.backendapi.hours.BillableHourRepository;
import com.example.backendapi.users.UserRepository;
import java.math.BigDecimal;
import java.time.LocalDate;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Component;

/**
 * Initializes sample data similarly to the legacy monolith.
 *
 * <p>Monolith behavior: seeds sample data at startup, primarily for demo/training usage.
 *
 * <p>This initializer is defensive: if DB is not configured/reachable, it silently does nothing so
 * /health still works.
 */
@Component
public class SampleDataInitializer implements ApplicationRunner {

    private final UserRepository userRepository;
    private final CustomerRepository customerRepository;
    private final BillingCategoryRepository billingCategoryRepository;
    private final BillableHourRepository billableHourRepository;

    public SampleDataInitializer(
            UserRepository userRepository,
            CustomerRepository customerRepository,
            BillingCategoryRepository billingCategoryRepository,
            BillableHourRepository billableHourRepository) {
        this.userRepository = userRepository;
        this.customerRepository = customerRepository;
        this.billingCategoryRepository = billingCategoryRepository;
        this.billableHourRepository = billableHourRepository;
    }

    @Override
    public void run(ApplicationArguments args) {
        // If DB isn't reachable (or schema not applied), skip seeding.
        try {
            if (userRepository.count() > 0) {
                return;
            }

            var u1 = userRepository.insert("john.doe@example.com", "John Doe");
            var u2 = userRepository.insert("jane.smith@example.com", "Jane Smith");

            var c1 = customerRepository.insert("Acme Corp", "billing@acme.com", "123 Business St");
            var c2 = customerRepository.insert("TechStart Inc", "finance@techstart.com", "456 Innovation Ave");
            var c3 = customerRepository.insert("MegaCorp Ltd", "accounts@megacorp.com", "789 Enterprise Blvd");

            var catDev = billingCategoryRepository.insert("Development", "Software development work", new BigDecimal("150.00"));
            var catConsult = billingCategoryRepository.insert("Consulting", "Business consulting services", new BigDecimal("200.00"));
            var catSupport = billingCategoryRepository.insert("Support", "Technical support and maintenance", new BigDecimal("100.00"));

            // A few recent hour entries, similar spirit to the monolith sample dataset.
            billableHourRepository.insert(c1.id(), u1.id(), catDev.id(), new BigDecimal("6.5"), "Initial build work", LocalDate.now().minusDays(3));
            billableHourRepository.insert(c1.id(), u2.id(), catConsult.id(), new BigDecimal("2.0"), "Stakeholder review", LocalDate.now().minusDays(2));
            billableHourRepository.insert(c2.id(), u1.id(), catSupport.id(), new BigDecimal("1.5"), "Support ticket triage", LocalDate.now().minusDays(1));
            billableHourRepository.insert(c3.id(), u2.id(), catDev.id(), new BigDecimal("4.0"), "Feature implementation", LocalDate.now());

        } catch (DataAccessException ex) {
            // Intentionally ignore: app must remain runnable even without DB.
        }
    }
}
