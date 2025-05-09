package com.ilhanozkan.libraryManagementSystem.model.exception;

import java.util.UUID;

public class UserIsNotActive extends RuntimeException {
  public UserIsNotActive(UUID id) {
    super("User with id " + id + " is not active");
  }
}
