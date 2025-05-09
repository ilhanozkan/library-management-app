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
public class AuthServiceImpl implements AuthService {
  private final PasswordEncoder passwordEncoder;
  private final UserRepository userRepository;
  private final AuthenticationManager authenticationManager;
  private final JwtService jwtService;

  @Override
  @Transactional
  public String register(RegisterRequestDTO registerRequestDTO) throws BadRequestException {
    if (userRepository.existsByUsername(registerRequestDTO.username()))
      throw new BadRequestException("Username is already in use");

    if (userRepository.existsByEmail(registerRequestDTO.email()))
      throw new BadRequestException("Email already exists");

    User user = User.builder()
        .name(registerRequestDTO.name())
        .surname(registerRequestDTO.surname())
        .email(registerRequestDTO.email())
        .username(registerRequestDTO.username())
        .password(passwordEncoder.encode(registerRequestDTO.password()))
        .build();

    userRepository.save(user);

    return "User " + user.getUsername() + " registered successfully";
  }

  @Override
  public ResponseEntity<?> login(LoginRequestDTO loginRequestDTO) {
    try {
    Authentication authentication = authenticationManager.authenticate(
        new UsernamePasswordAuthenticationToken(
            loginRequestDTO.getUsername(),
            loginRequestDTO.getPassword()
        )
    );

    UserDetails userDetails = (UserDetails) authentication.getPrincipal();
    String token = jwtService.generateToken(userDetails.getUsername());

    LoginResponseDTO loginResponseDTO = LoginResponseDTO.builder()
        .token(token)
        .username(userDetails.getUsername())
        .role(UserRole.valueOf(userDetails.getAuthorities().stream()
            .findFirst()
            .map(a -> a.getAuthority().replace("ROLE_", ""))
            .orElse(null)))
        .build();

    return ResponseEntity.ok(loginResponseDTO);
    } catch (Exception e) {
      return ResponseEntity.status(401).body("Invalid username or password");
    }
  }
}
