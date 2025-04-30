package com.ilhanozkan.libraryManagementSystem.model.dto.response;

import java.util.UUID;

public record BookResponseDTO(
    UUID id,
    String name,
    String isbn,
    String author,
    String publisher,
    Integer numberOfPages,
    Integer quantity
) {
}
