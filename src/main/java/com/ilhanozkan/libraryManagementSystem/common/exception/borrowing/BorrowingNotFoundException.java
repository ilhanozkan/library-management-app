package com.ilhanozkan.libraryManagementSystem.common.exception.borrowing;

import java.util.UUID;

public class BorrowingNotFoundException extends RuntimeException {
  public BorrowingNotFoundException(UUID id) {
    super("Borrowing with id " + id + " not found");
  }
}
