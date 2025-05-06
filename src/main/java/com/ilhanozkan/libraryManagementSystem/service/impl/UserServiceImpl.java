package com.ilhanozkan.libraryManagementSystem.service.impl;

import com.ilhanozkan.libraryManagementSystem.model.dto.request.UserRequestDTO;
import com.ilhanozkan.libraryManagementSystem.model.dto.response.UserResponseDTO;
import com.ilhanozkan.libraryManagementSystem.model.entity.User;
import com.ilhanozkan.libraryManagementSystem.model.mapper.UserResponseDTOMapper;
import com.ilhanozkan.libraryManagementSystem.repository.UserRepository;
import com.ilhanozkan.libraryManagementSystem.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class UserServiceImpl implements UserService {
  private final UserRepository userRepository;
  private final UserResponseDTOMapper mapper = UserResponseDTOMapper.INSTANCE;

  @Autowired
  public UserServiceImpl(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  public List<UserResponseDTO> getUsers() {
    return mapper.toUserResponseDTOList(userRepository.findAll());
  }

  public UserResponseDTO getUserById(UUID id) {
    return mapper.toUserResponseDTO(userRepository.findById(id)
        .orElseThrow(() -> new RuntimeException("User with id " + id + " not found")));
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

    String hashedPassword = new BCryptPasswordEncoder().encode(userRequestDTO.password());

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

  public void deleteUser(UUID id) {
    userRepository.deleteById(id);
  }
}
