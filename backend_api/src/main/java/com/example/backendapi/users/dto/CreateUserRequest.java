package com.example.backendapi.users.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

/** Request payload for creating a user. */
public record CreateUserRequest(
        @NotBlank(message = "name is required") String name,
        @NotBlank(message = "email is required") @Email(message = "email must be valid") String email) {}
