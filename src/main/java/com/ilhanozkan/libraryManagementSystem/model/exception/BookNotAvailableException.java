package com.ilhanozkan.libraryManagementSystem.model.exception;

import java.util.UUID;

public class BookNotAvailableException extends RuntimeException {
  public BookNotAvailableException(UUID id) {
    super("Book with id " + id + " not available");
  }
}
