package com.ilhanozkan.libraryManagementSystem.model.dto.request.auth;

public record RegisterRequestDTO(
    String username,
    String email,
    String password,
    String name,
    String surname
) {
}
