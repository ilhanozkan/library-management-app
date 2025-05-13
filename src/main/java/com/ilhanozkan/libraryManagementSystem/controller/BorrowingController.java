package com.ilhanozkan.libraryManagementSystem.controller;

import com.ilhanozkan.libraryManagementSystem.model.dto.request.BorrowingRequestDTO;
import com.ilhanozkan.libraryManagementSystem.model.dto.response.BorrowingResponseDTO;
import com.ilhanozkan.libraryManagementSystem.model.entity.User;
import com.ilhanozkan.libraryManagementSystem.model.entity.UserPrincipal;
import com.ilhanozkan.libraryManagementSystem.service.impl.BorrowingServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Description;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Tag(name = "Borrowing Operations")
@RestController
@RequestMapping("/borrowings")
@Slf4j
public class BorrowingController {
  private final BorrowingServiceImpl borrowingService;

  @Autowired
  public BorrowingController(BorrowingServiceImpl borrowingService) {
    this.borrowingService = borrowingService;
    log.info("BorrowingController initialized");
  }

  @Description("List user's all borrowings with filters")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Successfully retrieved user's all borrowings with filter"),
      @ApiResponse(responseCode = "403", description = "Access denied")
  })
  @GetMapping
  @PreAuthorize("hasRole('LIBRARIAN')")
  public List<BorrowingResponseDTO> getBorrowings() {
    try {
      log.info("Request to get all borrowings");
      List<BorrowingResponseDTO> borrowings = borrowingService.getBorrowings();
      log.info("Successfully retrieved {} borrowings", borrowings.size());
      return borrowings;
    } catch (RuntimeException e) {
      log.error("Error retrieving all borrowings", e);
      throw new RuntimeException(e.getMessage());
    }
  }

  @Operation(summary = "Get user's borrowings", description = "Retrieves all borrowings for a specific user")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Successfully retrieved user's borrowings"),
      @ApiResponse(responseCode = "404", description = "User not found"),
      @ApiResponse(responseCode = "403", description = "Access denied")
  })
  @GetMapping("/user/{userId}")
  @PreAuthorize("hasRole('LIBRARIAN')")
  public List<BorrowingResponseDTO> getBorrowingsByUserId(
      @Parameter(description = "ID of the user") @PathVariable UUID userId) {
    log.info("Request to get borrowings for user ID: {}", userId);
    List<BorrowingResponseDTO> borrowings = borrowingService.getBorrowingsByUserId(userId);
    log.info("Successfully retrieved {} borrowings for user ID: {}", borrowings.size(), userId);
    return borrowings;
  }

  @Operation(summary = "Get user's active borrowings", description = "Retrieves active borrowings for a specific user")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Successfully retrieved user's active borrowings"),
      @ApiResponse(responseCode = "404", description = "User not found"),
      @ApiResponse(responseCode = "403", description = "Access denied")
  })
  @GetMapping("/user/{userId}/active")
  @PreAuthorize("hasRole('LIBRARIAN')")
  public List<BorrowingResponseDTO> getActiveBorrowingsByUserId(
      @Parameter(description = "ID of the user") @PathVariable UUID userId) {
    log.info("Request to get active borrowings for user ID: {}", userId);
    List<BorrowingResponseDTO> activeBorrowings = borrowingService.getActiveBorrowingsByUserId(userId);
    log.info("Successfully retrieved {} active borrowings for user ID: {}", activeBorrowings.size(), userId);
    return activeBorrowings;
  }

  @Operation(summary = "Create new borrowing for librarians", description = "Creates a new book borrowing")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Successfully created borrowing"),
      @ApiResponse(responseCode = "400", description = "Invalid input or book not available"),
      @ApiResponse(responseCode = "403", description = "Access denied")
  })
  @PostMapping("/librarian")
  @PreAuthorize("hasRole('LIBRARIAN')")
  public ResponseEntity<?> createBorrowing(@ModelAttribute BorrowingRequestDTO borrowingRequestDTO) {
    log.info("Request to create borrowing - Book ID: {}, User ID: {}", 
             borrowingRequestDTO.getBookId(), borrowingRequestDTO.getUserId());
    try {
      BorrowingResponseDTO borrowing = borrowingService.createBorrowing(borrowingRequestDTO);
      log.info("Librarian user created a new borrowing successfully with ID: {}", borrowing.id());
      return ResponseEntity.ok(borrowing);
    } catch (RuntimeException e) {
      log.error("Librarian user get an error while creating borrowing - Book ID: {}, User ID: {}: {}",
                borrowingRequestDTO.getBookId(), borrowingRequestDTO.getUserId(), e.getMessage());
      return ResponseEntity.badRequest().body(e.getMessage());
    }
  }

  @Operation(summary = "Create new borrowing", description = "Creates a new book borrowing")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Successfully created borrowing"),
      @ApiResponse(responseCode = "400", description = "Invalid input or book not available"),
      @ApiResponse(responseCode = "403", description = "Access denied")
  })
  @PostMapping
  public ResponseEntity<?> createBorrowingPatrons(
      @RequestParam @Parameter(description = "Book ID") UUID bookId
  ) {
    log.info("User request to create borrowing - Book ID: {}", bookId);

    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
    User user = userPrincipal.getUser();

    try {
      BorrowingRequestDTO borrowingRequestDTO = new BorrowingRequestDTO(bookId, user.getId());
      BorrowingResponseDTO borrowing = borrowingService.createBorrowing(borrowingRequestDTO);

      log.info("Borrowing created successfully with ID: {}", borrowing.id());
      return ResponseEntity.ok(borrowing);
    } catch (RuntimeException e) {
      log.error("Error creating borrowing - Book ID: {}, User ID: {}: {}",
          bookId, user.getId(), e.getMessage());
      return ResponseEntity.badRequest().body(e.getMessage());
    }
  }

  @Operation(summary = "Return book", description = "Marks a borrowed book as returned")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Successfully returned book"),
      @ApiResponse(responseCode = "400", description = "Book already returned or invalid input")
  })
  @PutMapping("/{id}/return")
  public ResponseEntity<?> returnBook(
      @Parameter(description = "ID of the borrowing record") @PathVariable UUID id) {
    log.info("Request to return book for borrowing ID: {}", id);
    try {
      BorrowingResponseDTO borrowing = borrowingService.returnBook(id);
      log.info("Book returned successfully for borrowing ID: {}", id);
      return ResponseEntity.ok(borrowing);
    } catch (RuntimeException e) {
      log.error("Error returning book for borrowing ID: {}: {}", id, e.getMessage());
      return ResponseEntity.badRequest().body(e.getMessage());
    }
  }

  @Operation(summary = "Delete borrowing", description = "Deletes a borrowing record")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Successfully deleted borrowing"),
      @ApiResponse(responseCode = "404", description = "Borrowing not found"),
      @ApiResponse(responseCode = "403", description = "Access denied")
  })
  @DeleteMapping("/{id}")
  @PreAuthorize("hasRole('LIBRARIAN')")
  public ResponseEntity<Void> deleteBorrowing(
      @Parameter(description = "ID of the borrowing record to delete") @PathVariable UUID id) {
    log.info("Request to delete borrowing with ID: {}", id);
    try {
      borrowingService.deleteBorrowing(id);
      log.info("Borrowing deleted successfully: {}", id);
      return ResponseEntity.ok().build();
    } catch (RuntimeException e) {
      log.error("Error deleting borrowing with ID: {}: {}", id, e.getMessage());
      return ResponseEntity.notFound().build();
    }
  }

  @Operation(summary = "Get my borrowing history", description = "Retrieves all borrowings for the authenticated user")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Successfully retrieved borrowing history"),
      @ApiResponse(responseCode = "404", description = "User not found"),
  })
  @GetMapping("/my-history")
  public ResponseEntity<List<BorrowingResponseDTO>> getMyBorrowingHistory() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
    User user = userPrincipal.getUser();
    
    log.info("Request to get borrowing history for authenticated user ID: {}", user.getId());
    List<BorrowingResponseDTO> borrowings = borrowingService.getBorrowingsByUserId(user.getId());
    log.info("Successfully retrieved {} borrowings for authenticated user", borrowings.size());
    return ResponseEntity.ok(borrowings);
  }

  @Operation(summary = "Get my active borrowings", description = "Retrieves active borrowings for the authenticated user")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Successfully retrieved active borrowings"),
      @ApiResponse(responseCode = "404", description = "User not found"),
  })
  @GetMapping("/my-active")
  public ResponseEntity<List<BorrowingResponseDTO>> getMyActiveBorrowings() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
    User user = userPrincipal.getUser();
    
    log.info("Request to get active borrowings for authenticated user ID: {}", user.getId());
    List<BorrowingResponseDTO> activeBorrowings = borrowingService.getActiveBorrowingsByUserId(user.getId());
    log.info("Successfully retrieved {} active borrowings for authenticated user", activeBorrowings.size());
    return ResponseEntity.ok(activeBorrowings);
  }

  @Operation(summary = "Generate overdue books PDF report", description = "Generates a PDF report of overdue books")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Successfully generated overdue books report"),
      @ApiResponse(responseCode = "400", description = "Error generating report")
  })
  @GetMapping("/overdue-pdf-report")
  @PreAuthorize("hasRole('LIBRARIAN')")
  public ResponseEntity<?> getOverdueBooksPDFReport() {
    log.info("Request to generate overdue books PDF report");
    try {
      byte[] report = borrowingService.getOverdueBooksPDFReport();
      log.info("Successfully generated overdue books PDF report, size: {} bytes", report.length);
      return ResponseEntity.ok()
          .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=report.pdf")
          .contentType(MediaType.APPLICATION_PDF)
          .body(report);
    } catch (RuntimeException e) {
      log.error("Error generating overdue books PDF report", e);
      return ResponseEntity.badRequest().body(e.getMessage());
    } catch (Exception e) {
      log.error("Unexpected error generating overdue books PDF report", e);
      return ResponseEntity.status(500).body(null);
    }
  }

  @Operation(summary = "Generate overdue books text report", description = "Generates a text report of overdue books")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Successfully generated overdue books report"),
      @ApiResponse(responseCode = "400", description = "Error generating report")
  })
  @GetMapping("/overdue-report")
  @PreAuthorize("hasRole('LIBRARIAN')")
  public ResponseEntity<?> getOverdueBooksTextReport() {
    log.info("Request to generate overdue books text report");
    try {
      String report = borrowingService.getOverdueBooksTextReport();
      log.info("Successfully generated overdue books text report");
      return ResponseEntity.ok(report);
    } catch (RuntimeException e) {
      log.error("Error generating overdue books text report", e);
      return ResponseEntity.badRequest().body(e.getMessage());
    }
  }
}
