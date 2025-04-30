package com.ilhanozkan.libraryManagementSystem.controller;

import com.ilhanozkan.libraryManagementSystem.model.dto.request.UserRequestDTO;
import com.ilhanozkan.libraryManagementSystem.model.dto.response.UserResponseDTO;
import com.ilhanozkan.libraryManagementSystem.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Description;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Tag(name = "User Operations")
@RestController
@RequestMapping("/users")
public class UserController {
  private final UserService userService;

  @Autowired
  public UserController(UserService userService) {
    this.userService = userService;
  }

  @GetMapping
  @Description("Fetch all users")
  public List<UserResponseDTO> getUsers() {
    try {
      return userService.getUsers();
    } catch (RuntimeException e) {
      throw new RuntimeException(e.getMessage());
    }
  }

  @GetMapping("/{id}")
  @Description("Fetch a user by id")
  public UserResponseDTO getUserById(@PathVariable UUID id) {
    try {
      return userService.getUserById(id);
    } catch (RuntimeException e) {
      throw new RuntimeException(e.getMessage());
    }
  }

  @Operation(
      summary = "Create a user",
      description = "Create a new user",
      responses = {
          @ApiResponse(responseCode = "200", description = "User created successfully",
              content = @Content(mediaType = "application/json",
                  schema = @Schema(implementation = UserRequestDTO.class))),
          @ApiResponse(responseCode = "400", description = "User could not be created")
      }
  )
  @PostMapping
  public UserResponseDTO createUser(@Valid @RequestBody UserRequestDTO userRequestDTO) {
    try {
      return userService.createUser(userRequestDTO);
    } catch (RuntimeException e) {
      throw new RuntimeException(e.getMessage());
    }
  }


}
