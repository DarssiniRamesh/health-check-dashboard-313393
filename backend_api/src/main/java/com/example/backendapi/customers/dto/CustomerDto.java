package com.example.backendapi.customers.dto;

import java.time.Instant;

/** Customer DTO returned by the API. */
public record CustomerDto(Long id, String name, String email, String address, Instant createdAt) {}
