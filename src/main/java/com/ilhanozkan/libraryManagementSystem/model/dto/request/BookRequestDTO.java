package com.ilhanozkan.libraryManagementSystem.model.dto.request;

public record BookRequestDTO(
    String name,
    String isbn,
    String author,
    String publisher,
    Integer numberOfPages,
    Integer quantity
) {
}
