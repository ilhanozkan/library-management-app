package com.ilhanozkan.libraryManagementSystem.model.dto.response;

import java.time.LocalDateTime;
import java.util.UUID;

public record BorrowingResponseDTO(
    UUID id,
    BookResponseDTO book,
    UserResponseDTO user,
    LocalDateTime borrowDate,
    LocalDateTime dueDate,
    LocalDateTime returnDate,
    Boolean returned
) {
}
