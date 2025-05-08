package com.ilhanozkan.libraryManagementSystem.controller;

import com.ilhanozkan.libraryManagementSystem.model.dto.request.UserRequestDTO;
import com.ilhanozkan.libraryManagementSystem.model.dto.response.UserResponseDTO;
import com.ilhanozkan.libraryManagementSystem.security.JwtService;
import com.ilhanozkan.libraryManagementSystem.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
  private UserService userService;
  private JwtService jwtService;

  @PostMapping("/register")
  public UserResponseDTO register(@RequestBody UserRequestDTO requestDTO) {
    return userService.createUser(requestDTO);
  }

  @PostMapping("/login")
  public UserResponseDTO login(@RequestBody UserRequestDTO requestDTO) {
    return userService.login(requestDTO);
  }
}
