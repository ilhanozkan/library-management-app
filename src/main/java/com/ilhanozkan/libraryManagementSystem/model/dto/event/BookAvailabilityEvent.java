package com.ilhanozkan.libraryManagementSystem.model.dto.event;

import java.util.UUID;

public record BookAvailabilityEvent(
    UUID bookId,
    String bookName,
    String isbn,
    Integer totalQuantity,
    Integer availableQuantity,
    Long timestamp
) {
    public static BookAvailabilityEvent create(UUID bookId, String bookName, String isbn, Integer totalQuantity, Integer availableQuantity) {
        return new BookAvailabilityEvent(bookId, bookName, isbn, totalQuantity, availableQuantity, System.currentTimeMillis());
    }
} 