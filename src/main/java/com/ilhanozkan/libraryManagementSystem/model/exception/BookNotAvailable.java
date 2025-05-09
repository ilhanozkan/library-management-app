package com.ilhanozkan.libraryManagementSystem.model.exception;

import java.util.UUID;

public class BookNotAvailable extends RuntimeException {
  public BookNotAvailable(UUID id) {
    super("Book with id " + id + " not available");
  }
}
