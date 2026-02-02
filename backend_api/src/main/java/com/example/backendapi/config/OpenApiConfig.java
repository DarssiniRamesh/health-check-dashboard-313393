package com.example.backendapi.config;

import com.example.backendapi.api.ApiConstants;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.context.annotation.Configuration;

/** OpenAPI/Swagger configuration for the backend REST API. */
@Configuration
@OpenAPIDefinition(
        info =
                @Info(
                        title = "backend_api",
                        version = "0.1.0",
                        description =
                                "Spring Boot backend implementing the migrated endpoints from the legacy monolith (users/customers/categories/hours/reports) plus health checks."),
        tags = {
            @Tag(name = ApiConstants.TAG_DASHBOARD, description = "Dashboard summary endpoints"),
            @Tag(name = ApiConstants.TAG_USERS, description = "User CRUD endpoints"),
            @Tag(name = ApiConstants.TAG_CUSTOMERS, description = "Customer CRUD endpoints"),
            @Tag(name = ApiConstants.TAG_CATEGORIES, description = "Billing category CRUD endpoints"),
            @Tag(name = ApiConstants.TAG_HOURS, description = "Billable hour logging and listing endpoints"),
            @Tag(name = ApiConstants.TAG_REPORTS, description = "Reporting endpoints (customer, monthly, revenue)")
        })
public class OpenApiConfig {}
