package com.ilhanozkan.libraryManagementSystem.model.exception;

import java.util.UUID;

public class BookNotFound extends RuntimeException {
  public BookNotFound(UUID id) {
    super("Book with id " + id + " not found");
  }
}
