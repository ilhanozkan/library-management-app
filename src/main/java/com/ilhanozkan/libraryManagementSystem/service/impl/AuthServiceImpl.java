package com.ilhanozkan.libraryManagementSystem.service.impl;

import com.ilhanozkan.libraryManagementSystem.model.dto.request.auth.LoginRequestDTO;
import com.ilhanozkan.libraryManagementSystem.model.dto.request.auth.RegisterRequestDTO;
import com.ilhanozkan.libraryManagementSystem.model.dto.response.auth.LoginResponseDTO;
import com.ilhanozkan.libraryManagementSystem.model.entity.User;
import com.ilhanozkan.libraryManagementSystem.model.enums.UserRole;
import com.ilhanozkan.libraryManagementSystem.repository.UserRepository;
import com.ilhanozkan.libraryManagementSystem.security.JwtService;
import com.ilhanozkan.libraryManagementSystem.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.BadRequestException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {
  private final PasswordEncoder passwordEncoder;
  private final UserRepository userRepository;
  private final AuthenticationManager authenticationManager;
  private final JwtService jwtService;

  @Override
  @Transactional
  public String register(RegisterRequestDTO registerRequestDTO) throws BadRequestException {
    log.debug("Processing registration request for username: {}", registerRequestDTO.username());
    
    if (userRepository.existsByUsername(registerRequestDTO.username())) {
      log.warn("Registration failed: Username '{}' already in use", registerRequestDTO.username());
      throw new BadRequestException("Username is already in use");
    }

    if (userRepository.existsByEmail(registerRequestDTO.email())) {
      log.warn("Registration failed: Email '{}' already exists", registerRequestDTO.email());
      throw new BadRequestException("Email already exists");
    }

    if (registerRequestDTO.password().length() < 8) {
      log.warn("Registration failed: Password must be at least 8 characters long");
      throw new BadRequestException("Password must be at least 8 characters long");
    }

    if (!registerRequestDTO.email().matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
      log.warn("Registration failed: Invalid email format");
      throw new BadRequestException("Invalid email format");
    }

    User user = User.builder()
        .name(registerRequestDTO.name())
        .surname(registerRequestDTO.surname())
        .email(registerRequestDTO.email())
        .username(registerRequestDTO.username())
        .password(passwordEncoder.encode(registerRequestDTO.password()))
        .build();

    user = userRepository.save(user);
    log.info("User registered successfully with ID: {}", user.getId());

    return "User " + user.getUsername() + " registered successfully";
  }

  @Override
  public ResponseEntity<?> login(LoginRequestDTO loginRequestDTO) {
    log.debug("Processing login request for username: {}", loginRequestDTO.getUsername());
    try {
      Authentication authentication = authenticationManager.authenticate(
          new UsernamePasswordAuthenticationToken(
              loginRequestDTO.getUsername(),
              loginRequestDTO.getPassword()
          )
      );

      UserDetails userDetails = (UserDetails) authentication.getPrincipal();
      String token = jwtService.generateToken(userDetails.getUsername());
      log.info("User '{}' authenticated successfully, token generated", userDetails.getUsername());

      String role = userDetails.getAuthorities().stream()
          .findFirst()
          .map(a -> a.getAuthority().replace("ROLE_", ""))
          .orElse(null);
      
      log.debug("User '{}' has role: {}", userDetails.getUsername(), role);

      LoginResponseDTO loginResponseDTO = LoginResponseDTO.builder()
          .token(token)
          .username(userDetails.getUsername())
          .role(UserRole.valueOf(role))
          .build();

      return ResponseEntity.ok(loginResponseDTO);
    } catch (Exception e) {
      log.error("Authentication failed for username: {}", loginRequestDTO.getUsername(), e);
      return ResponseEntity.status(401).body("Invalid username or password");
    }
  }
}
