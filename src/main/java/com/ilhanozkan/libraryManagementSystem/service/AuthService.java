package com.ilhanozkan.libraryManagementSystem.service;

import com.ilhanozkan.libraryManagementSystem.model.dto.request.auth.LoginRequestDTO;
import com.ilhanozkan.libraryManagementSystem.model.dto.request.auth.RegisterRequestDTO;
import com.ilhanozkan.libraryManagementSystem.model.dto.response.auth.LoginResponseDTO;
import org.apache.coyote.BadRequestException;

public interface AuthService {
  public String register(RegisterRequestDTO registerRequestDTO) throws BadRequestException;
  public LoginResponseDTO login(LoginRequestDTO loginRequestDTO);
}
