package com.ilhanozkan.libraryManagementSystem.controller;

import com.ilhanozkan.libraryManagementSystem.model.dto.request.BookRequestDTO;
import com.ilhanozkan.libraryManagementSystem.model.dto.response.BookResponseDTO;
import com.ilhanozkan.libraryManagementSystem.service.impl.BookServiceImpl;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Book Operations")
@RestController
@RequestMapping("/books")
public class BookController {
  private final BookServiceImpl bookService;

  public BookController(BookServiceImpl bookService) {
    this.bookService = bookService;
  }

  @GetMapping
  public List<BookResponseDTO> getBooks() {
    return bookService.getBooks();
  }

  @PostMapping
  public ResponseEntity<?> createBook(@Valid @RequestBody BookRequestDTO bookRequestDTO) {
    try {
      return ResponseEntity.ok(bookService.createBook(bookRequestDTO));
    } catch (RuntimeException e) {
      return ResponseEntity.badRequest().body(e.getMessage());

    }
  }
}
