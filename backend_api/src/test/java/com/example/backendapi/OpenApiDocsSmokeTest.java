package com.example.backendapi;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.backendapi.bootstrap.SampleDataInitializer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.ResponseEntity;

/**
 * Smoke tests for OpenAPI generation endpoints.
 *
 * <p>This test uses a real embedded web server with test configuration that excludes datasource
 * autoconfiguration and database-dependent components. The OpenAPI documentation should be
 * generated without requiring a database connection.
 */
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        classes = backendapiApplication.class)
@ComponentScan(
        basePackages = "com.example.backendapi",
        excludeFilters =
                @ComponentScan.Filter(
                        type = FilterType.ASSIGNABLE_TYPE,
                        classes = {
                            SampleDataInitializer.class,
                            com.example.backendapi.users.UserRepository.class,
                            com.example.backendapi.customers.CustomerRepository.class,
                            com.example.backendapi.categories.BillingCategoryRepository.class,
                            com.example.backendapi.hours.BillableHourRepository.class,
                            com.example.backendapi.reports.ReportsRepository.class,
                            com.example.backendapi.health.HealthService.class,
                            com.example.backendapi.users.UsersService.class,
                            com.example.backendapi.customers.CustomersService.class,
                            com.example.backendapi.categories.BillingCategoriesService.class,
                            com.example.backendapi.hours.BillableHoursService.class,
                            com.example.backendapi.reports.ReportsService.class,
                            com.example.backendapi.dashboard.DashboardService.class,
                            com.example.backendapi.users.UsersController.class,
                            com.example.backendapi.customers.CustomersController.class,
                            com.example.backendapi.categories.BillingCategoriesController.class,
                            com.example.backendapi.hours.BillableHoursController.class,
                            com.example.backendapi.reports.ReportsController.class,
                            com.example.backendapi.dashboard.DashboardController.class,
                            com.example.backendapi.health.HealthController.class
                        }))
class OpenApiDocsSmokeTest {

    @Autowired private TestRestTemplate restTemplate;

    @Test
    void v3ApiDocsShouldReturn200() {
        ResponseEntity<String> response = restTemplate.getForEntity("/v3/api-docs", String.class);

        assertThat(response.getStatusCode().value()).isEqualTo(200);
        assertThat(response.getBody()).isNotBlank();
        // Minimal sanity check: OpenAPI JSON should contain the openapi version field.
        assertThat(response.getBody()).contains("\"openapi\"");
    }
}
