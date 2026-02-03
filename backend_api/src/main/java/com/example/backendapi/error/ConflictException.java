package com.example.backendapi.error;

/** Thrown when a request conflicts with current state (e.g., unique constraint). */
public class ConflictException extends RuntimeException {

    public ConflictException(String message) {
        super(message);
    }
}
