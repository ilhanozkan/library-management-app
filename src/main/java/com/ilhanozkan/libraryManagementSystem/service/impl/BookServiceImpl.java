package com.ilhanozkan.libraryManagementSystem.service.impl;

import com.ilhanozkan.libraryManagementSystem.model.dto.request.BookRequestDTO;
import com.ilhanozkan.libraryManagementSystem.model.dto.response.BookResponseDTO;
import com.ilhanozkan.libraryManagementSystem.model.entity.Book;
import com.ilhanozkan.libraryManagementSystem.model.mapper.BookResponseDTOMapper;
import com.ilhanozkan.libraryManagementSystem.repository.BookRepository;
import com.ilhanozkan.libraryManagementSystem.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class BookServiceImpl implements BookService {
  private final BookRepository bookRepository;
  private final BookResponseDTOMapper mapper = BookResponseDTOMapper.INSTANCE;

  @Autowired
  public BookServiceImpl(BookRepository bookRepository) {
    this.bookRepository = bookRepository;
  }

  public List<BookResponseDTO> getBooks() {
    return mapper.toBookResponseDTOList(bookRepository.findAll());
  }

  private Book findBookById(UUID id) {
    return bookRepository.findById(id).orElseThrow(() -> new RuntimeException("Book with id " + id + " not found"));
  }

  public BookResponseDTO getBookById(UUID id) {
    return mapper.toBookResponseDTO(findBookById(id));
  }

  public BookResponseDTO createBook(BookRequestDTO bookRequestDTO) {
    Book newBook = Book.builder()
        .isbn(bookRequestDTO.isbn())
        .name(bookRequestDTO.name())
        .author(bookRequestDTO.author())
        .publisher(bookRequestDTO.publisher())
        .quantity(bookRequestDTO.quantity())
        .numberOfPages(bookRequestDTO.numberOfPages())
        .build();

    return mapper.toBookResponseDTO(bookRepository.save(newBook));
  }

  public BookResponseDTO updateBook(UUID id, BookRequestDTO bookRequestDTO) {
    Book updatedBook = findBookById(id);

    updatedBook.setIsbn(bookRequestDTO.isbn());
    updatedBook.setName(bookRequestDTO.name());
    updatedBook.setAuthor(bookRequestDTO.author());
    updatedBook.setPublisher(bookRequestDTO.publisher());
    updatedBook.setQuantity(bookRequestDTO.quantity());
    updatedBook.setNumberOfPages(bookRequestDTO.numberOfPages());

    return mapper.toBookResponseDTO(bookRepository.save(updatedBook));
  }
}
