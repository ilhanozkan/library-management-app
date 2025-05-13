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
    Boolean returned,
    String bookName,
    String userFullName
) {
    public BorrowingResponseDTO(
        UUID id,
        BookResponseDTO book,
        UserResponseDTO user,
        LocalDateTime borrowDate,
        LocalDateTime dueDate,
        LocalDateTime returnDate,
        Boolean returned
    ) {
        this(id, book, user, borrowDate, dueDate, returnDate, returned, 
             book != null ? book.name() : null, 
             user != null ? user.name() + " " + user.surname() : null);
    }
}
