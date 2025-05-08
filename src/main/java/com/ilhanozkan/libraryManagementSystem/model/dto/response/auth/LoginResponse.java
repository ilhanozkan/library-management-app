package com.ilhanozkan.libraryManagementSystem.model.dto.response.auth;

import com.ilhanozkan.libraryManagementSystem.model.enums.UserRole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponse {
  private String token;
  private String username;
  private UserRole role;
}
