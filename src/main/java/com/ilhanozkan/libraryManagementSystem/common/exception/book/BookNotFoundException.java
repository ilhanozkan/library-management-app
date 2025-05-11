package com.ilhanozkan.libraryManagementSystem.common.exception.book;

import java.util.UUID;

public class BookNotFoundException extends RuntimeException {
  public BookNotFoundException(UUID id) {
    super("Book with id " + id + " not found");
  }

  public BookNotFoundException(String message) {
    super(message);
  }
}
