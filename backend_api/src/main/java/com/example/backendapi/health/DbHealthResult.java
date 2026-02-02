package com.example.backendapi.health;

import java.util.Map;

/** Result of a database connectivity health check. */
public record DbHealthResult(boolean isUp, Map<String, Object> details) {

    // PUBLIC_INTERFACE
    public static DbHealthResult up(Map<String, Object> details) {
        /** Constructs an UP database health result. */
        return new DbHealthResult(true, details == null ? Map.of() : details);
    }

    // PUBLIC_INTERFACE
    public static DbHealthResult down(Map<String, Object> details) {
        /** Constructs a DOWN database health result. */
        return new DbHealthResult(false, details == null ? Map.of() : details);
    }
}
