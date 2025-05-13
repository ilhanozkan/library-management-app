package com.ilhanozkan.libraryManagementSystem.common.exception.book;

import java.util.UUID;

public class BookAlreadyReturnedException extends RuntimeException {
  public BookAlreadyReturnedException(UUID id) {
    super("Book with id " + id + " has already been returned");
  }
}
