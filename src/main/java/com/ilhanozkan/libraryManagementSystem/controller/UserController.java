package com.ilhanozkan.libraryManagementSystem.controller;

import com.ilhanozkan.libraryManagementSystem.model.dto.request.UserRequestDTO;
import com.ilhanozkan.libraryManagementSystem.model.dto.response.PagedResponse;
import com.ilhanozkan.libraryManagementSystem.model.dto.response.UserResponseDTO;
import com.ilhanozkan.libraryManagementSystem.service.impl.UserServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Description;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Tag(name = "User Operations")
@RestController
@RequestMapping("/users")
@Slf4j
public class UserController {
  private final UserServiceImpl userService;

  @Autowired
  public UserController(UserServiceImpl userService) {
    this.userService = userService;
    log.info("UserController initialized");
  }

  @Operation(summary = "Get all users with pagination", description = "Retrieves a paginated list of all library users")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Successfully retrieved users")
  })
  @Description("Fetch all users")
  @GetMapping
  public PagedResponse<UserResponseDTO> getUsersPaged(@PageableDefault(size = 10) Pageable pageable) {
    log.info("Request to get all users with pagination - page: {}, size: {}", pageable.getPageNumber(), pageable.getPageSize());
    try {
      PagedResponse<UserResponseDTO> response = userService.getUsers(pageable);
      log.info("Successfully retrieved {} users (page {} of {})", 
               response.getContent().size(), response.getPage(), response.getTotalPages());
      return response;
    } catch (RuntimeException e) {
      log.error("Error retrieving users with pagination", e);
      throw new RuntimeException(e.getMessage());
    }
  }

  @Operation(summary = "Get user by ID", description = "Retrieves a specific user by their ID")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Successfully retrieved user"),
      @ApiResponse(responseCode = "404", description = "User not found")
  })
  @Description("Fetch a user by id")
  @GetMapping("/{id}")
  public UserResponseDTO getUserById(@PathVariable UUID id) {
    log.info("Request to get user by ID: {}", id);
    try {
      UserResponseDTO user = userService.getUserById(id);
      log.info("Successfully retrieved user: {}", user.username());
      return user;
    } catch (RuntimeException e) {
      log.error("Error retrieving user with ID: {}", id, e);
      throw new RuntimeException(e.getMessage());
    }
  }

  @Operation(
      summary = "Create a user",
      description = "Create a new user",
      responses = {
          @ApiResponse(responseCode = "201", description = "User created successfully",
              content = @Content(mediaType = "application/json",
                  schema = @Schema(implementation = UserRequestDTO.class))),
          @ApiResponse(responseCode = "400", description = "User could not be created")
      }
  )
  @PostMapping
  public ResponseEntity<?> createUser(@Valid @RequestBody UserRequestDTO userRequestDTO) {
    log.info("Request to create user with username: {}", userRequestDTO.username());
    try {
      UserResponseDTO response = userService.createUser(userRequestDTO);
      log.info("User created successfully: {}", response.username());
      return ResponseEntity.status(HttpStatus.CREATED).body(response);
    } catch (RuntimeException e) {
      log.error("Error creating user: {}", e.getMessage());
      return ResponseEntity.badRequest().body(e.getMessage());
    }
  }

  @Operation(summary = "Get user by email", description = "Retrieves a specific user by their email")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Successfully retrieved user"),
      @ApiResponse(responseCode = "404", description = "User not found")
  })
  @GetMapping("/email/{email}")
  public ResponseEntity<?> getUserByEmail(
      @Parameter(description = "Email of the user to retrieve") @PathVariable String email) {
    log.info("Request to get user by email: {}", email);
    try {
      UserResponseDTO user = userService.getUserByEmail(email);
      log.info("Successfully retrieved user by email: {}", user.username());
      return ResponseEntity.ok(user);
    } catch (Exception e) {
      log.error("Error retrieving user with email: {}", email, e);
      return ResponseEntity.badRequest().body(e.getMessage());
    }
  }

  @Operation(summary = "Update user", description = "Updates an existing user's information")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Successfully updated user"),
      @ApiResponse(responseCode = "404", description = "User not found"),
      @ApiResponse(responseCode = "400", description = "Invalid input")
  })
  @PutMapping("/{id}")
  public ResponseEntity<UserResponseDTO> updateUser(
      @Parameter(description = "ID of the user to update") @PathVariable UUID id,
      @Parameter(description = "Updated user details") @Valid @RequestBody UserRequestDTO userRequestDTO) {
    log.info("Request to update user with ID: {}", id);
    try {
      UserResponseDTO updatedUser = userService.updateUser(id, userRequestDTO);
      log.info("User updated successfully: {}", updatedUser.username());
      return ResponseEntity.ok(updatedUser);
    } catch (Exception e) {
      log.error("Error updating user with ID: {}", id, e);
      return ResponseEntity.notFound().build();
    }
  }

  @Operation(summary = "Delete user", description = "Deletes a user from the library")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Successfully deleted user"),
      @ApiResponse(responseCode = "404", description = "User not found")
  })
  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteUser(
      @Parameter(description = "ID of the user to delete") @PathVariable UUID id) {
    log.info("Request to delete user with ID: {}", id);
    try {
      userService.deleteUser(id);
      log.info("User deleted successfully: {}", id);
      return ResponseEntity.ok().build();
    } catch (Exception e) {
      log.error("Error deleting user with ID: {}", id, e);
      return ResponseEntity.notFound().build();
    }
  }

  @Operation(summary = "Deactivate user", description = "Deactivates a user's account")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Successfully deactivated user"),
      @ApiResponse(responseCode = "404", description = "User not found")
  })
  @PutMapping("/{id}/deactivate")
  public ResponseEntity<UserResponseDTO> deactivateUser(
      @Parameter(description = "ID of the user to deactivate") @PathVariable UUID id) {
    log.info("Request to deactivate user with ID: {}", id);
    try {
      UserResponseDTO deactivatedUser = userService.deactivateUser(id);
      log.info("User deactivated successfully: {}", deactivatedUser.username());
      return ResponseEntity.ok(deactivatedUser);
    } catch (Exception e) {
      log.error("Error deactivating user with ID: {}", id, e);
      return ResponseEntity.notFound().build();
    }
  }
}
