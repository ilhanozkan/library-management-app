package com.ilhanozkan.libraryManagementSystem.controller;

import com.ilhanozkan.libraryManagementSystem.model.dto.request.BookQuantityUpdateDTO;
import com.ilhanozkan.libraryManagementSystem.model.dto.request.BookRequestDTO;
import com.ilhanozkan.libraryManagementSystem.model.enums.BookGenre;
import com.ilhanozkan.libraryManagementSystem.service.BookService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/books")
@Tag(name = "Book Management", description = "APIs for managing books in the library")
@Slf4j
public class BookController {
    private final BookService bookService;

    @Operation(summary = "Get all books with pagination", description = "Retrieves a list of all books in the library")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved books")
    })
    @GetMapping
    public ResponseEntity<?> getAllBooksPaged(@PageableDefault(size = 10) Pageable pageable) {
      try {
        return ResponseEntity.ok(bookService.getAllBooks(pageable));
      } catch (RuntimeException e) {
        log.error("Error retrieving all books", e);
        return ResponseEntity.badRequest().body(e.getMessage());
      }
    }

    @Operation(summary = "Search books", description = "Search books by title, author, ISBN, or genre")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved books")
    })
    @GetMapping("/search")
    public ResponseEntity<?> searchBooks(
            @Parameter(description = "Title to search for") @RequestParam(required = false) String title,
            @Parameter(description = "Author to search for") @RequestParam(required = false) String author,
            @Parameter(description = "ISBN to search for") @RequestParam(required = false) String isbn,
            @Parameter(description = "Genre to search for (case sensitive, must match enum value)") @RequestParam(required = false) String genre) {
        try {
            return ResponseEntity.ok(bookService.searchBooks(title, author, isbn, genre));
        } catch (RuntimeException e) {
            log.error("Error searching books", e);
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @Operation(summary = "Get book by ID", description = "Retrieves a specific book by its ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved book"),
        @ApiResponse(responseCode = "404", description = "Book not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<?> getBookById(
            @Parameter(description = "ID of the book to retrieve") @PathVariable UUID id) {
      try {
        return ResponseEntity.ok(bookService.getBookById(id));
      } catch (RuntimeException e) {
        log.error("Error retrieving book with ID {}", id, e);
        return ResponseEntity.badRequest().body(e.getMessage());
      }
    }

    @Operation(summary = "Get book by ISBN", description = "Retrieves a specific book by its ISBN")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved book"),
        @ApiResponse(responseCode = "404", description = "Book not found")
    })
    @GetMapping("/isbn/{isbn}")
    public ResponseEntity<?> getBookByIsbn(
            @Parameter(description = "ISBN of the book to retrieve") @PathVariable String isbn) {
      try {
        return ResponseEntity.ok(bookService.getBookByIsbn(isbn));
      } catch (RuntimeException e) {
        log.error("Error retrieving book with ISBN {}", isbn, e);
        return ResponseEntity.badRequest().body(e.getMessage());
      }
    }

    @Operation(summary = "Create new book", description = "Creates a new book in the library")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully created book"),
        @ApiResponse(responseCode = "400", description = "Invalid input")
    })
    @PostMapping
    @PreAuthorize("hasRole('LIBRARIAN')")
    public ResponseEntity<?> createBook(
            @Parameter(description = "Book details to create") @Valid @RequestBody BookRequestDTO bookRequestDTO) {
      try {
        if (bookRequestDTO.getGenre() == null) {
            log.error("Book genre is null");
            return ResponseEntity.badRequest().body("Book genre is required");
        }
        return ResponseEntity.ok(bookService.createBook(bookRequestDTO));
      } catch (RuntimeException e) {
        log.error("Error creating book", e);
        if (e.getMessage() != null && e.getMessage().contains("book_genre") && e.getMessage().contains("not-null")) {
            return ResponseEntity.badRequest().body("Book genre must be specified. Please choose from the available genres: " + 
                                                  Arrays.toString(BookGenre.values()));
        }
        return ResponseEntity.badRequest().body(e.getMessage());
      }
    }

    @Operation(summary = "Update book", description = "Updates an existing book in the library")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully updated book"),
        @ApiResponse(responseCode = "404", description = "Book not found"),
        @ApiResponse(responseCode = "400", description = "Invalid input")
    })
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('LIBRARIAN')")
    public ResponseEntity<?> updateBook(
            @Parameter(description = "ID of the book to update") @PathVariable UUID id,
            @Parameter(description = "Updated book details") @Valid @RequestBody BookRequestDTO bookDetails) {
        try {
            return ResponseEntity.ok(bookService.updateBook(id, bookDetails));
        } catch (RuntimeException e) {
            log.error("Error updating book with ID {}", id, e);
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @Operation(summary = "Update book available quantity", description = "Updates only the available quantity of an existing book")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully updated book quantity"),
        @ApiResponse(responseCode = "404", description = "Book not found"),
        @ApiResponse(responseCode = "400", description = "Invalid input or quantity exceeds total quantity")
    })
    @PutMapping("/{id}/available-quantity")
    @PreAuthorize("hasRole('LIBRARIAN')")
    public ResponseEntity<?> updateBookAvailableQuantity(
            @Parameter(description = "ID of the book to update") @PathVariable UUID id,
            @Parameter(description = "New available quantity") @Valid @RequestBody BookQuantityUpdateDTO quantityUpdateDTO) {
        try {
            return ResponseEntity.ok(bookService.updateBookAvailableQuantity(id, quantityUpdateDTO));
        } catch (IllegalArgumentException e) {
            log.error("Invalid quantity update for book with ID {}: {}", id, e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (RuntimeException e) {
            log.error("Error updating book quantity with ID {}", id, e);
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "Delete book", description = "Deletes a book from the library")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully deleted book"),
        @ApiResponse(responseCode = "404", description = "Book not found")
    })
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('LIBRARIAN')")
    public ResponseEntity<?> deleteBook(
            @Parameter(description = "ID of the book to delete") @PathVariable UUID id) {
        try {
            bookService.deleteBook(id);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            log.error("Error deleting book with ID {}", id, e);
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
} 