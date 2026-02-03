package com.example.backendapi.error;

import java.time.Instant;
import java.util.Map;

/** Standard error response returned by API endpoints. */
public record ApiErrorResponse(String error, String message, String timestamp, Map<String, Object> details) {

    // PUBLIC_INTERFACE
    public static ApiErrorResponse of(String error, String message, Map<String, Object> details) {
        /** Creates a standard API error response. */
        return new ApiErrorResponse(
                error, message, Instant.now().toString(), details == null ? Map.of() : details);
    }
}
