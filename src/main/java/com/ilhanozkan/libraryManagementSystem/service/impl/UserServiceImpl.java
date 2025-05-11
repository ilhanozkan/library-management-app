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
public class UserServiceImpl implements UserService {
  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  private final UserResponseDTOMapper mapper = UserResponseDTOMapper.INSTANCE;

  public PagedResponse<UserResponseDTO> getUsers(Pageable pageable) {
    Page<User> usersPage = userRepository.findAll(pageable);
    List<UserResponseDTO> userResponseDTOs = mapper.toUserResponseDTOList(usersPage.getContent());

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
    return mapper.toUserResponseDTO(userRepository.findById(id)
        .orElseThrow(() -> new UserNotFoundException(id)));
  }

  @Transactional
  public UserResponseDTO getUserByEmail(String email) {
    Optional<User> user = userRepository.findByEmail(email);

    if (user.isEmpty())
      throw new ResourceNotFoundException("User", "email", email);

    return mapper.toUserResponseDTO(user.get());
  }

  public UserResponseDTO createUser(UserRequestDTO userRequestDTO) {
    if (userRequestDTO.password() == null || userRequestDTO.password().isEmpty())
      throw new RuntimeException("Password is required");

    if (userRequestDTO.email() == null || userRequestDTO.email().isEmpty())
      throw new RuntimeException("Email is required");

    if (userRequestDTO.password().length() < 8)
      throw new RuntimeException("Password length should be at least 8 characters");

    if (userRepository.existsByUsername(userRequestDTO.username()))
      throw new RuntimeException("Username already exists");

    if (userRepository.existsByEmail(userRequestDTO.email()))
      throw new RuntimeException("Email already exists");

    String hashedPassword = passwordEncoder.encode(userRequestDTO.password());

    User savedUser = User.builder()
        .name(userRequestDTO.name())
        .surname(userRequestDTO.surname())
        .username(userRequestDTO.username())
        .email(userRequestDTO.email())
        .password(hashedPassword)
        .role(userRequestDTO.role())
        .build();

    return mapper.toUserResponseDTO(userRepository.save(savedUser));
  }

  @Transactional
  public UserResponseDTO updateUser(UUID id, UserRequestDTO userRequestDTO) throws BadRequestException {
    User user = userRepository.findById(id)
        .orElseThrow(() -> new UserNotFoundException(id));

    // Check username uniqueness
    if (userRequestDTO.username() != null && !userRequestDTO.username().equals(user.getUsername())) {
      if (userRepository.existsByUsername(userRequestDTO.username()))
        throw new BadRequestException("Username already exists");

      user.setUsername(userRequestDTO.username());
    }

    // Check email uniqueness
    if (userRequestDTO.email() != null && !userRequestDTO.email().equals(user.getEmail())) {
      if (userRepository.existsByEmail(userRequestDTO.email()))
        throw new BadRequestException("Email already exists");

      user.setEmail(userRequestDTO.email());
    }

    // Update password if provided
    if (userRequestDTO.password() != null)
      user.setPassword(passwordEncoder.encode(userRequestDTO.password()));

    // Update role if provided
    if (userRequestDTO.role() != null)
      user.setRole(userRequestDTO.role());

    // Update user status
    if (userRequestDTO.status() != null)
      user.setStatus(userRequestDTO.status());

    // Update member fields if provided
    if (userRequestDTO.name() != null) {
      user.setName(userRequestDTO.name());
    }

    if (userRequestDTO.surname() != null) {
      user.setSurname(userRequestDTO.surname());
    }

    User updatedUser = userRepository.save(user);
    return mapper.toUserResponseDTO(updatedUser);
  }

  @Transactional
  public void deleteUser(UUID id) {
    User user = userRepository.findById(id)
        .orElseThrow(() -> new UserNotFoundException(id));
    userRepository.delete(user);
  }

  @Transactional
  public UserResponseDTO deactivateUser(UUID id) {
    User user = userRepository.findById(id)
        .orElseThrow(() -> new UserNotFoundException(id));

    user.setStatus(UserStatus.INACTIVE);
    return mapper.toUserResponseDTO(userRepository.save(user));
  }
}
