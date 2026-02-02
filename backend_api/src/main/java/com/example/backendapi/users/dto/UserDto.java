package com.example.backendapi.users.dto;

/** User DTO returned by the API. */
public record UserDto(Long id, String email, String name) {}
