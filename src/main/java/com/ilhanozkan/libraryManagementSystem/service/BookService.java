package com.ilhanozkan.libraryManagementSystem.service;

import com.ilhanozkan.libraryManagementSystem.model.dto.request.BookRequestDTO;
import com.ilhanozkan.libraryManagementSystem.model.dto.response.BookResponseDTO;

import java.util.List;
import java.util.UUID;

public interface BookService {
  public List<BookResponseDTO> getBooks();
  public BookResponseDTO getBookById(UUID id);
  public BookResponseDTO createBook(BookRequestDTO bookRequestDTO);
  public BookResponseDTO updateBook(UUID id, BookRequestDTO bookRequestDTO);
}
