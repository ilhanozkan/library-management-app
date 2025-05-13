package com.ilhanozkan.libraryManagementSystem.service;

import com.ilhanozkan.libraryManagementSystem.model.dto.request.UserRequestDTO;
import com.ilhanozkan.libraryManagementSystem.model.dto.response.PagedResponse;
import com.ilhanozkan.libraryManagementSystem.model.dto.response.UserResponseDTO;
import org.apache.coyote.BadRequestException;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface UserService {
  public PagedResponse<UserResponseDTO> getUsers(Pageable pageable);
  public UserResponseDTO getUserById(UUID id);
  public UserResponseDTO createUser(UserRequestDTO userRequestDTO);
  public UserResponseDTO updateUser(UUID id, UserRequestDTO userRequestDTO) throws BadRequestException;
  public void deleteUser(UUID id);
  public UserResponseDTO deactivateUser(UUID id);
}
