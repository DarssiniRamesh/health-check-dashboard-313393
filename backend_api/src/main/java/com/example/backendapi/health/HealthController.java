package com.example.backendapi.health;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.time.Instant;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Health-check endpoints for service and database reachability.
 *
 * <p>These endpoints are intended for the Angular frontend to verify end-to-end connectivity.
 */
@RestController
@RequestMapping
@Tag(name = "Health", description = "Health check endpoints for service and database connectivity")
public class HealthController {

    private final HealthService healthService;

    public HealthController(HealthService healthService) {
        this.healthService = healthService;
    }

    // PUBLIC_INTERFACE
    @GetMapping("/health")
    @Operation(
            summary = "Overall service health",
            description =
                    "Returns overall service health. HTTP 200 when healthy, HTTP 503 when unhealthy.")
    public ResponseEntity<Map<String, Object>> health() {
        HealthResult result = healthService.checkServiceHealth();
        HttpStatus status = result.isUp() ? HttpStatus.OK : HttpStatus.SERVICE_UNAVAILABLE;

        return ResponseEntity.status(status)
                .body(
                        Map.of(
                                "status",
                                result.isUp() ? "UP" : "DOWN",
                                "timestamp",
                                Instant.now().toString(),
                                "details",
                                result.details()));
    }

    // PUBLIC_INTERFACE
    @GetMapping("/status")
    @Operation(
            summary = "Database connectivity status",
            description =
                    "Checks database connectivity. HTTP 200 when DB is reachable, HTTP 503 otherwise.")
    public ResponseEntity<Map<String, Object>> status() {
        DbHealthResult result = healthService.checkDatabaseHealth();
        HttpStatus status = result.isUp() ? HttpStatus.OK : HttpStatus.SERVICE_UNAVAILABLE;

        return ResponseEntity.status(status)
                .body(
                        Map.of(
                                "database",
                                result.isUp() ? "UP" : "DOWN",
                                "timestamp",
                                Instant.now().toString(),
                                "details",
                                result.details()));
    }

    // PUBLIC_INTERFACE
    @GetMapping("/health/db")
    @Operation(
            summary = "Database connectivity health",
            description =
                    "Alias for /status. Checks database connectivity. HTTP 200 when DB is reachable, HTTP 503 otherwise.")
    public ResponseEntity<Map<String, Object>> healthDb() {
        return status();
    }
}
