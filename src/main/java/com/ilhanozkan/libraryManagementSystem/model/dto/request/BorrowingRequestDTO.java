package com.ilhanozkan.libraryManagementSystem.model.dto.request;

import java.util.UUID;

public record BorrowingRequestDTO(
  UUID bookId
) {
}
