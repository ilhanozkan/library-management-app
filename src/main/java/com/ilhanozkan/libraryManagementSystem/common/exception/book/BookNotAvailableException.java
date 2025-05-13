package com.ilhanozkan.libraryManagementSystem.common.exception.book;

import java.util.UUID;

public class BookNotAvailableException extends RuntimeException {
  public BookNotAvailableException(UUID id) {
    super("Book with id " + id + " not available");
  }
}
