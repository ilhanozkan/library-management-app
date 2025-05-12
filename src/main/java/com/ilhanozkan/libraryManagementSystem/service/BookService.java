package com.ilhanozkan.libraryManagementSystem.service;

import com.ilhanozkan.libraryManagementSystem.model.dto.request.BookQuantityUpdateDTO;
import com.ilhanozkan.libraryManagementSystem.model.dto.request.BookRequestDTO;
import com.ilhanozkan.libraryManagementSystem.model.dto.response.BookResponseDTO;
import com.ilhanozkan.libraryManagementSystem.model.dto.response.PagedResponse;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface BookService {
  public PagedResponse<BookResponseDTO> getAllBooks(Pageable pageable);
  public BookResponseDTO getBookById(UUID id);
  public BookResponseDTO getBookByIsbn(String isbn);
  public List<BookResponseDTO> searchBooks(String title, String author, String isbn, String genre);
  public BookResponseDTO createBook(BookRequestDTO bookRequestDTO);
  public BookResponseDTO updateBook(UUID id, BookRequestDTO bookRequestDTO);
  public void updateBookQuantity(UUID id, int change);
  public BookResponseDTO updateBookAvailableQuantity(UUID id, BookQuantityUpdateDTO quantityUpdateDTO);
  public void deleteBook(UUID id);
}
