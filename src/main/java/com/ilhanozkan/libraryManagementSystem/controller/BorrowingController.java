package com.ilhanozkan.libraryManagementSystem.controller;

import com.ilhanozkan.libraryManagementSystem.model.dto.request.BorrowingRequestDTO;
import com.ilhanozkan.libraryManagementSystem.model.dto.response.BorrowingResponseDTO;
import com.ilhanozkan.libraryManagementSystem.service.impl.BorrowingServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Description;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Tag(name = "Borrowing Operations")
@RestController
@RequestMapping("/borrowings")
public class BorrowingController {
  private final BorrowingServiceImpl borrowingService;

  @Autowired
  public BorrowingController(BorrowingServiceImpl borrowingService) {
    this.borrowingService = borrowingService;
  }

  @Description("List user's all borrowings with filters")
  @GetMapping
  public List<BorrowingResponseDTO> getBorrowings() {
    try {
      return borrowingService.getBorrowings();
    } catch (RuntimeException e) {
      throw new RuntimeException(e.getMessage());
    }
  }

  @Operation(summary = "Get user's borrowings", description = "Retrieves all borrowings for a specific user")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Successfully retrieved user's borrowings"),
      @ApiResponse(responseCode = "404", description = "User not found")
  })
  @GetMapping("/user/{userId}")
  public List<BorrowingResponseDTO> getBorrowingsByUserId(
      @Parameter(description = "ID of the user") @PathVariable UUID userId) {
    return borrowingService.getBorrowingsByUserId(userId);
  }

  @Operation(summary = "Get user's active borrowings", description = "Retrieves active borrowings for a specific user")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Successfully retrieved user's active borrowings"),
      @ApiResponse(responseCode = "404", description = "User not found")
  })
  @GetMapping("/user/{userId}/active")
  public List<BorrowingResponseDTO> getActiveBorrowingsByUserId(
      @Parameter(description = "ID of the user") @PathVariable UUID userId) {
    return borrowingService.getActiveBorrowingsByUserId(userId);
  }

  @Operation(summary = "Create new borrowing", description = "Creates a new book borrowing")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Successfully created borrowing"),
      @ApiResponse(responseCode = "400", description = "Invalid input or book not available")
  })
  @PostMapping
  public ResponseEntity<?> createBorrowing(@ModelAttribute BorrowingRequestDTO borrowingRequestDTO) {
    try {
      return ResponseEntity.ok(borrowingService.createBorrowing(borrowingRequestDTO));
    } catch (RuntimeException e) {
      return ResponseEntity.badRequest().body(e.getMessage());
    }
  }

  @Operation(summary = "Return book", description = "Marks a borrowed book as returned")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Successfully returned book"),
      @ApiResponse(responseCode = "400", description = "Book already returned or invalid input")
  })
  @PutMapping("/{id}/return")
  public ResponseEntity<BorrowingResponseDTO> returnBook(
      @Parameter(description = "ID of the borrowing record") @PathVariable UUID id) {
    try {
      return ResponseEntity.ok(borrowingService.returnBook(id));
    } catch (RuntimeException e) {
      return ResponseEntity.badRequest().build();
    }
  }

  @Operation(summary = "Delete borrowing", description = "Deletes a borrowing record")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Successfully deleted borrowing"),
      @ApiResponse(responseCode = "404", description = "Borrowing not found")
  })
  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteBorrowing(
      @Parameter(description = "ID of the borrowing record to delete") @PathVariable UUID id) {
    try {
      borrowingService.deleteBorrowing(id);
      return ResponseEntity.ok().build();
    } catch (RuntimeException e) {
      return ResponseEntity.notFound().build();
    }
  }
}
