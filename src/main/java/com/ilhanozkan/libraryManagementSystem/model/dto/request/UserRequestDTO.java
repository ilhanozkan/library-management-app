package com.ilhanozkan.libraryManagementSystem.model.dto.request;

import com.ilhanozkan.libraryManagementSystem.model.enums.UserRole;
import com.ilhanozkan.libraryManagementSystem.model.enums.UserStatus;

public record UserRequestDTO(
    String username,
    String email,
    String password,
    String name,
    String surname,
    UserRole role,
    UserStatus status
) {
}
