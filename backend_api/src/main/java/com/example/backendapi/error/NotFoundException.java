package com.example.backendapi.error;

/** Thrown when a requested entity does not exist. */
public class NotFoundException extends RuntimeException {

    public NotFoundException(String message) {
        super(message);
    }
}
