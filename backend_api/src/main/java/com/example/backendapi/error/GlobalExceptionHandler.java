package com.example.backendapi.error;

import java.util.HashMap;
import java.util.Map;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.BadSqlGrammarException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/** Global exception handling for REST controllers. */
@RestControllerAdvice
public class GlobalExceptionHandler {

    // PUBLIC_INTERFACE
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorResponse> handleValidation(MethodArgumentNotValidException ex) {
        /** Returns 400 validation errors with per-field details. */
        Map<String, Object> details = new HashMap<>();
        for (FieldError fieldError : ex.getBindingResult().getFieldErrors()) {
            details.put(fieldError.getField(), fieldError.getDefaultMessage());
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiErrorResponse.of("VALIDATION_ERROR", "Request validation failed", details));
    }

    // PUBLIC_INTERFACE
    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleNotFound(NotFoundException ex) {
        /** Returns 404 errors. */
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiErrorResponse.of("NOT_FOUND", ex.getMessage(), Map.of()));
    }

    // PUBLIC_INTERFACE
    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<ApiErrorResponse> handleConflict(ConflictException ex) {
        /** Returns 409 errors. */
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(ApiErrorResponse.of("CONFLICT", ex.getMessage(), Map.of()));
    }

    // PUBLIC_INTERFACE
    @ExceptionHandler({DataIntegrityViolationException.class})
    public ResponseEntity<ApiErrorResponse> handleConstraintViolation(DataIntegrityViolationException ex) {
        /**
         * Returns 409 for typical unique/foreign key errors.
         *
         * <p>This mirrors the monolith's behavior of failing the operation, but provides a more explicit
         * JSON response for the migrated frontend.
         */
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(ApiErrorResponse.of("DATA_INTEGRITY_VIOLATION", "Database constraint violated", Map.of()));
    }

    // PUBLIC_INTERFACE
    @ExceptionHandler({BadSqlGrammarException.class})
    public ResponseEntity<ApiErrorResponse> handleSqlGrammar(BadSqlGrammarException ex) {
        /** Returns 500 with a stable message; details contain SQLState when available. */
        Map<String, Object> details = new HashMap<>();
        if (ex.getSQLException() != null) {
            details.put("sqlState", ex.getSQLException().getSQLState());
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiErrorResponse.of("SQL_ERROR", "Database query failed", details));
    }

    // PUBLIC_INTERFACE
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponse> handleGeneric(Exception ex) {
        /** Returns 500 for unexpected errors. */
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiErrorResponse.of("INTERNAL_ERROR", "An unexpected error occurred", Map.of()));
    }
}
