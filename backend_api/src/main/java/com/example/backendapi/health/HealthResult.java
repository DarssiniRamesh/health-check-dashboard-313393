package com.example.backendapi.health;

import java.util.Map;

/** Result of a general service health check. */
public record HealthResult(boolean isUp, Map<String, Object> details) {

    // PUBLIC_INTERFACE
    public static HealthResult up(Map<String, Object> details) {
        /** Constructs an UP health result. */
        return new HealthResult(true, details == null ? Map.of() : details);
    }

    // PUBLIC_INTERFACE
    public static HealthResult down(Map<String, Object> details) {
        /** Constructs a DOWN health result. */
        return new HealthResult(false, details == null ? Map.of() : details);
    }
}
