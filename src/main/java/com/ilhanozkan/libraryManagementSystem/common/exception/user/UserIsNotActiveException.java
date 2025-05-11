package com.ilhanozkan.libraryManagementSystem.common.exception.user;

import java.util.UUID;

public class UserIsNotActiveException extends RuntimeException {
  public UserIsNotActiveException(UUID id) {
    super("User with id " + id + " is not active");
  }
}
