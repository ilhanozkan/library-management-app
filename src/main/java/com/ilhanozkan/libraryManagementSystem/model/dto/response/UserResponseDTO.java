package com.ilhanozkan.libraryManagementSystem.model.dto.response;

import com.ilhanozkan.libraryManagementSystem.model.enums.UserRole;

import java.util.UUID;

public record UserResponseDTO(
    UUID id,
    String username,
    String email,
    String name,
    String surname,
    UserRole role
) {
}
