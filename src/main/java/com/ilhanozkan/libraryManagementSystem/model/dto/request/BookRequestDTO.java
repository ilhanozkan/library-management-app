package com.ilhanozkan.libraryManagementSystem.model.dto.request;

import com.ilhanozkan.libraryManagementSystem.model.enums.BookGenre;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BookRequestDTO {
  @NotBlank(message = "Book name is required")
  private String name;

  // Validation for ISBN-13 format
  @NotBlank(message = "ISBN is required")
  @jakarta.validation.constraints.Pattern(
      regexp = "^(978|979)\\d{10}$",
      message = "ISBN must be in the format 978XXXXXXXXX or 979XXXXXXXXX")
  private String isbn;

  @NotBlank(message = "Author is required")
  private String author;

  @NotBlank(message = "Publisher is required")
  private String publisher;

  @NotNull(message = "Number of pages is required")
  @Min(value = 0, message = "Number of pages must be at least 0")
  private Integer numberOfPages;

  @NotNull(message = "Quantity is required")
  @Min(value = 0, message = "Quantity must be at least 0")
  private Integer quantity;

  @NotNull(message = "Book genre is required")
  private BookGenre genre;

}
