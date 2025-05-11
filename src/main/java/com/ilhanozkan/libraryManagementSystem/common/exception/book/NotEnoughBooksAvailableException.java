package com.ilhanozkan.libraryManagementSystem.common.exception.book;

public class NotEnoughBooksAvailableException extends RuntimeException {
  public NotEnoughBooksAvailableException() {
    super("Not enough books available");
  }
}
