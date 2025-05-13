package com.ilhanozkan.libraryManagementSystem.service.impl;

import com.ilhanozkan.libraryManagementSystem.common.exception.user.UserNotFoundException;
import com.ilhanozkan.libraryManagementSystem.model.dto.request.UserRequestDTO;
import com.ilhanozkan.libraryManagementSystem.model.dto.response.PagedResponse;
import com.ilhanozkan.libraryManagementSystem.model.dto.response.UserResponseDTO;
import com.ilhanozkan.libraryManagementSystem.model.entity.User;
import com.ilhanozkan.libraryManagementSystem.model.enums.UserStatus;
import com.ilhanozkan.libraryManagementSystem.common.exception.ResourceNotFoundException;
import com.ilhanozkan.libraryManagementSystem.model.mapper.UserResponseDTOMapper;
import com.ilhanozkan.libraryManagementSystem.repository.UserRepository;
import com.ilhanozkan.libraryManagementSystem.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.BadRequestException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {
  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  private final UserResponseDTOMapper mapper = UserResponseDTOMapper.INSTANCE;

  public PagedResponse<UserResponseDTO> getUsers(Pageable pageable) {
    log.debug("Fetching users with pagination: page={}, size={}", pageable.getPageNumber(), pageable.getPageSize());
    Page<User> usersPage = userRepository.findAll(pageable);
    List<UserResponseDTO> userResponseDTOs = mapper.toUserResponseDTOList(usersPage.getContent());
    
    log.debug("Retrieved {} users out of {} total", userResponseDTOs.size(), usersPage.getTotalElements());
    return PagedResponse.<UserResponseDTO>builder()
        .content(userResponseDTOs)
        .page(usersPage.getNumber())
        .size(usersPage.getSize())
        .totalElements(usersPage.getTotalElements())
        .totalPages(usersPage.getTotalPages())
        .last(usersPage.isLast())
        .build();
  }

  public UserResponseDTO getUserById(UUID id) {
    log.debug("Getting user by ID: {}", id);
    User user = userRepository.findById(id)
        .orElseThrow(() -> {
          log.warn("User not found with ID: {}", id);
          return new UserNotFoundException(id);
        });
    log.debug("Found user: {}", user.getUsername());
    return mapper.toUserResponseDTO(user);
  }

  @Transactional
  public UserResponseDTO getUserByEmail(String email) {
    log.debug("Getting user by email: {}", email);
    
    // Validate the email format
    if (email == null || email.trim().isEmpty()) {
      log.warn("Email validation failed: email is required");
      throw new RuntimeException("Email is required");
    }

    if (!email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
      log.warn("Email validation failed: invalid email format: {}", email);
      throw new RuntimeException("Email is not valid");
    }

    Optional<User> user = userRepository.findByEmail(email);
    if (user.isEmpty()) {
      log.warn("User not found with email: {}", email);
      throw new ResourceNotFoundException("User", "email", email);
    }

    log.debug("Found user with email {}: {}", email, user.get().getUsername());
    return mapper.toUserResponseDTO(user.get());
  }

  public UserResponseDTO createUser(UserRequestDTO userRequestDTO) {
    log.info("Creating new user with username: {}", userRequestDTO.username());
    
    if (userRequestDTO.password() == null || userRequestDTO.password().isEmpty()) {
      log.warn("User creation failed: password is required");
      throw new RuntimeException("Password is required");
    }

    if (userRequestDTO.email() == null || userRequestDTO.email().trim().isEmpty()) {
      log.warn("User creation failed: email is required");
      throw new RuntimeException("Email is required");
    }

    // Check if the email is valid
    if (!userRequestDTO.email().matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
      log.warn("User creation failed: invalid email format: {}", userRequestDTO.email());
      throw new RuntimeException("Email is not valid");
    }

    if (userRequestDTO.password().length() < 8) {
      log.warn("User creation failed: password too short");
      throw new RuntimeException("Password length should be at least 8 characters");
    }

    if (userRepository.existsByUsername(userRequestDTO.username())) {
      log.warn("User creation failed: username '{}' already exists", userRequestDTO.username());
      throw new RuntimeException("Username already exists");
    }

    if (userRepository.existsByEmail(userRequestDTO.email())) {
      log.warn("User creation failed: email '{}' already exists", userRequestDTO.email());
      throw new RuntimeException("Email already exists");
    }

    log.debug("Encoding password for new user");
    String hashedPassword = passwordEncoder.encode(userRequestDTO.password());

    User savedUser = User.builder()
        .name(userRequestDTO.name())
        .surname(userRequestDTO.surname())
        .username(userRequestDTO.username())
        .email(userRequestDTO.email())
        .password(hashedPassword)
        .role(userRequestDTO.role())
        .build();

    savedUser = userRepository.save(savedUser);
    log.info("User created successfully with ID: {}", savedUser.getId());
    return mapper.toUserResponseDTO(savedUser);
  }

  @Transactional
  public UserResponseDTO updateUser(UUID id, UserRequestDTO userRequestDTO) throws BadRequestException {
    log.info("Updating user with ID: {}", id);
    User user = userRepository.findById(id)
        .orElseThrow(() -> {
          log.warn("User not found with ID: {}", id);
          return new UserNotFoundException(id);
        });

    // Check username uniqueness
    if (userRequestDTO.username() != null && !userRequestDTO.username().equals(user.getUsername())) {
      log.debug("Updating username from '{}' to '{}'", user.getUsername(), userRequestDTO.username());
      if (userRepository.existsByUsername(userRequestDTO.username())) {
        log.warn("User update failed: username '{}' already exists", userRequestDTO.username());
        throw new BadRequestException("Username already exists");
      }

      user.setUsername(userRequestDTO.username());
    }

    // Check email uniqueness
    if (userRequestDTO.email() != null && !userRequestDTO.email().equals(user.getEmail())) {
      log.debug("Updating email from '{}' to '{}'", user.getEmail(), userRequestDTO.email());
      if (userRepository.existsByEmail(userRequestDTO.email())) {
        log.warn("User update failed: email '{}' already exists", userRequestDTO.email());
        throw new BadRequestException("Email already exists");
      }

      user.setEmail(userRequestDTO.email());
    }

    // Update password if provided
    if (userRequestDTO.password() != null) {
      log.debug("Updating password for user ID: {}", id);
      user.setPassword(passwordEncoder.encode(userRequestDTO.password()));
    }

    // Update role if provided
    if (userRequestDTO.role() != null) {
      log.debug("Updating role from '{}' to '{}'", user.getRole(), userRequestDTO.role());
      user.setRole(userRequestDTO.role());
    }

    // Update user status
    if (userRequestDTO.status() != null) {
      log.debug("Updating status from '{}' to '{}'", user.getStatus(), userRequestDTO.status());
      user.setStatus(userRequestDTO.status());
    }

    // Update member fields if provided
    if (userRequestDTO.name() != null) {
      log.debug("Updating name from '{}' to '{}'", user.getName(), userRequestDTO.name());
      user.setName(userRequestDTO.name());
    }

    if (userRequestDTO.surname() != null) {
      log.debug("Updating surname from '{}' to '{}'", user.getSurname(), userRequestDTO.surname());
      user.setSurname(userRequestDTO.surname());
    }

    User updatedUser = userRepository.save(user);
    log.info("User updated successfully: {}", updatedUser.getUsername());
    return mapper.toUserResponseDTO(updatedUser);
  }

  @Transactional
  public void deleteUser(UUID id) {
    log.info("Deleting user with ID: {}", id);
    User user = userRepository.findById(id)
        .orElseThrow(() -> {
          log.warn("Cannot delete: User not found with ID: {}", id);
          return new UserNotFoundException(id);
        });
    
    log.debug("Found user to delete: {}", user.getUsername());
    userRepository.delete(user);
    log.info("User deleted successfully: {}", user.getUsername());
  }

  @Transactional
  public UserResponseDTO deactivateUser(UUID id) {
    log.info("Deactivating user with ID: {}", id);
    User user = userRepository.findById(id)
        .orElseThrow(() -> {
          log.warn("Cannot deactivate: User not found with ID: {}", id);
          return new UserNotFoundException(id);
        });

    log.debug("Changing user status from {} to {}", user.getStatus(), UserStatus.INACTIVE);
    user.setStatus(UserStatus.INACTIVE);
    User deactivatedUser = userRepository.save(user);
    log.info("User deactivated successfully: {}", user.getUsername());
    return mapper.toUserResponseDTO(deactivatedUser);
  }
}
