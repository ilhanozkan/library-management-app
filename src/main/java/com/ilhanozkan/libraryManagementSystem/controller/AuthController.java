package com.ilhanozkan.libraryManagementSystem.controller;

import com.ilhanozkan.libraryManagementSystem.model.dto.request.auth.LoginRequestDTO;
import com.ilhanozkan.libraryManagementSystem.model.dto.request.auth.RegisterRequestDTO;
import com.ilhanozkan.libraryManagementSystem.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
  private final AuthService authService;

  @Operation(summary = "Register new user", description = "Registers a new user")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Successfully registered user"),
      @ApiResponse(responseCode = "400", description = "Invalid input or username/email already exists")
  })
  @PostMapping("/register")
  public ResponseEntity<?> register(@RequestBody RegisterRequestDTO requestDTO) throws BadRequestException {
    try {
      return ResponseEntity.ok(authService.register(requestDTO));
    } catch (RuntimeException e) {
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
    try {
      return ResponseEntity.ok(authService.login(requestDTO));
    } catch (RuntimeException e) {
      return ResponseEntity.status(401).body("Invalid username or password");
    }
  }
}
