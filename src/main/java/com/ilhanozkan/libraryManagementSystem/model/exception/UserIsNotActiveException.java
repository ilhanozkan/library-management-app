package com.ilhanozkan.libraryManagementSystem.model.exception;

import java.util.UUID;

public class UserIsNotActiveException extends RuntimeException {
  public UserIsNotActiveException(UUID id) {
    super("User with id " + id + " is not active");
  }
}
