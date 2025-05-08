package com.ilhanozkan.libraryManagementSystem.service;

import com.ilhanozkan.libraryManagementSystem.model.dto.request.auth.LoginRequestDTO;
import com.ilhanozkan.libraryManagementSystem.model.dto.request.auth.RegisterRequestDTO;
import com.ilhanozkan.libraryManagementSystem.model.dto.response.auth.LoginResponse;

public interface AuthService {
  public String register(RegisterRequestDTO registerRequestDTO);
  public LoginResponse login(LoginRequestDTO loginRequestDTO);
}
