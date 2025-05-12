package com.ilhanozkan.libraryManagementSystem.model.dto.response;

import com.ilhanozkan.libraryManagementSystem.model.enums.BookGenre;

import java.util.UUID;

public record BookResponseDTO(
    UUID id,
    String name,
    String isbn,
    String author,
    String publisher,
    Integer numberOfPages,
    Integer quantity,
    Integer availableQuantity,
    BookGenre genre
) {
}
