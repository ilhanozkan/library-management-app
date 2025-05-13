package com.ilhanozkan.libraryManagementSystem.controller;

import com.ilhanozkan.libraryManagementSystem.model.dto.request.auth.LoginRequestDTO;
import com.ilhanozkan.libraryManagementSystem.model.dto.request.auth.RegisterRequestDTO;
import com.ilhanozkan.libraryManagementSystem.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.BadRequestException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {
  private final AuthService authService;

  @Operation(summary = "Register new user", description = "Registers a new user")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Successfully registered user"),
      @ApiResponse(responseCode = "400", description = "Invalid input or username/email already exists")
  })
  @PostMapping("/register")
  public ResponseEntity<?> register(@RequestBody RegisterRequestDTO requestDTO) throws BadRequestException {
    log.info("Registration attempt for username: {}", requestDTO.username());
    try {
      ResponseEntity<?> response = ResponseEntity.ok(authService.register(requestDTO));
      log.info("User registered successfully: {}", requestDTO.username());
      return response;
    } catch (RuntimeException e) {
      log.error("Registration failed for username {}: {}", requestDTO.username(), e.getMessage());
      return ResponseEntity.badRequest().body(e.getMessage());
    }
  }

  @Operation(summary = "Login user", description = "Authenticates user and return JWT token")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Successfully authenticated"),
      @ApiResponse(responseCode = "401", description = "Invalid credentials")
  })
  @PostMapping("/login")
  public ResponseEntity<?> login(@RequestBody LoginRequestDTO requestDTO) {
    log.info("Login attempt for username: {}", requestDTO.getUsername());
    try {
      ResponseEntity<?> response = ResponseEntity.ok(authService.login(requestDTO));
      log.info("User logged in successfully: {}", requestDTO.getUsername());
      return response;
    } catch (RuntimeException e) {
      log.error("Login failed for username {}: {}", requestDTO.getUsername(), e.getMessage());
      return ResponseEntity.status(401).body("Invalid username or password");
    }
  }
}
