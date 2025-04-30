package com.ilhanozkan.libraryManagementSystem.model.dto.response;

import com.ilhanozkan.libraryManagementSystem.model.enums.UserRole;

public record UserResponseDTO(
    String username,
    String email,
    String name,
    String surname,
    UserRole role
) {
}
