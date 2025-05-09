package com.ilhanozkan.libraryManagementSystem.model.exception;

import java.util.UUID;

public class UserNotFound extends RuntimeException {
  public UserNotFound(UUID id) {
    super("User with id " + id + " not found");
  }
}
