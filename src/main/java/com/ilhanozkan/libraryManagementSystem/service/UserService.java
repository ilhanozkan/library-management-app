package com.ilhanozkan.libraryManagementSystem.service;

import com.ilhanozkan.libraryManagementSystem.model.dto.request.UserRequestDTO;
import com.ilhanozkan.libraryManagementSystem.model.dto.response.UserResponseDTO;

import java.util.List;
import java.util.UUID;

public interface UserService {
  public List<UserResponseDTO> getUsers();
  public UserResponseDTO getUserById(UUID id);
  public UserResponseDTO createUser(UserRequestDTO userRequestDTO);
  public void deleteUser(UUID id);
}
