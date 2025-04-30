package com.ilhanozkan.libraryManagementSystem.model.dto.response;

import java.time.LocalDateTime;

public record BorrowingResponseDTO(
  BookResponseDTO book,
  UserResponseDTO user,
  LocalDateTime borrowDate,
  LocalDateTime dueDate,
  LocalDateTime returnDate,
  Boolean returned
) {
}
