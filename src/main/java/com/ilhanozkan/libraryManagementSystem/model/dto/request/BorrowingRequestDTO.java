package com.ilhanozkan.libraryManagementSystem.model.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.UUID;

@AllArgsConstructor
@Data
public class BorrowingRequestDTO {
  @Schema(description = "ID of the book to borrow")
  private UUID bookId;
  @Schema(description = "ID of the user borrowing the book")
  private UUID userId;
}
