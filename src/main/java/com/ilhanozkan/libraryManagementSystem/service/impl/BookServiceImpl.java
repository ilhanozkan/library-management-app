package com.ilhanozkan.libraryManagementSystem.service.impl;

import com.ilhanozkan.libraryManagementSystem.common.exception.book.BookNotFoundException;
import com.ilhanozkan.libraryManagementSystem.common.exception.book.NotEnoughBooksAvailableException;
import com.ilhanozkan.libraryManagementSystem.model.dto.request.BookRequestDTO;
import com.ilhanozkan.libraryManagementSystem.model.dto.response.BookResponseDTO;
import com.ilhanozkan.libraryManagementSystem.model.dto.response.BorrowingResponseDTO;
import com.ilhanozkan.libraryManagementSystem.model.dto.response.PagedResponse;
import com.ilhanozkan.libraryManagementSystem.model.entity.Book;
import com.ilhanozkan.libraryManagementSystem.model.enums.BookGenre;
import com.ilhanozkan.libraryManagementSystem.model.mapper.BookResponseDTOMapper;
import com.ilhanozkan.libraryManagementSystem.repository.BookRepository;
import com.ilhanozkan.libraryManagementSystem.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

  public PagedResponse<BookResponseDTO> getAllBooks(Pageable pageable) {
    Page<Book> booksPage = bookRepository.findAll(pageable);
    List<Book> bookResponses = booksPage.getContent();

    return PagedResponse.<BookResponseDTO>builder()
        .content(mapper.toBookResponseDTOList(bookResponses))
        .page(booksPage.getNumber())
        .size(booksPage.getSize())
        .totalElements(booksPage.getTotalElements())
        .totalPages(booksPage.getTotalPages())
        .last(booksPage.isLast())
        .build();

  }

  // Search books by title, author, ISBN, or genre
  public List<BookResponseDTO> searchBooks(String title, String author, String isbn, String genre) {
    // If all parameters are null, return all books
    if (title == null && author == null && isbn == null && genre == null) {
      return mapper.toBookResponseDTOList(bookRepository.findAll());
    }
    
    // Convert genre string to enum ordinal value if provided
    String genreValue = null;
    if (genre != null && !genre.trim().isEmpty()) {
      try {
        BookGenre genreEnum = BookGenre.valueOf(genre.toUpperCase());
        genreValue = String.valueOf(genreEnum.ordinal());
      } catch (IllegalArgumentException e) {
        // Invalid genre provided, will return empty list
        return List.of();
      }
    }
    
    return mapper.toBookResponseDTOList(bookRepository.searchBooks(title, author, isbn, genreValue));
  }

  private Book findBookById(UUID id) {
    return bookRepository.findById(id).orElseThrow(() -> new BookNotFoundException(id));
  }

  public BookResponseDTO getBookById(UUID id) {
    return mapper.toBookResponseDTO(findBookById(id));
  }
  
  public BookResponseDTO getBookByIsbn(String isbn) {
    Book book = bookRepository.findByIsbn(isbn);
    if (book == null)
        throw new BookNotFoundException("Book with ISBN " + isbn + " not found");

    return mapper.toBookResponseDTO(book);
  }

  public BookResponseDTO createBook(BookRequestDTO bookRequestDTO) {
    Book newBook = Book.builder()
        .isbn(bookRequestDTO.getIsbn())
        .name(bookRequestDTO.getName())
        .author(bookRequestDTO.getAuthor())
        .publisher(bookRequestDTO.getPublisher())
        .quantity(bookRequestDTO.getQuantity())
        .availableQuantity(bookRequestDTO.getQuantity())
        .numberOfPages(bookRequestDTO.getNumberOfPages())
        .genre(bookRequestDTO.getGenre())
        .build();

    return mapper.toBookResponseDTO(bookRepository.save(newBook));
  }

  @Transactional
  public BookResponseDTO updateBook(UUID id, BookRequestDTO bookRequestDTO) {
    Book updatedBook = findBookById(id);

    updatedBook.setIsbn(bookRequestDTO.getIsbn());
    updatedBook.setName(bookRequestDTO.getName());
    updatedBook.setAuthor(bookRequestDTO.getAuthor());
    updatedBook.setPublisher(bookRequestDTO.getPublisher());
    updatedBook.setQuantity(bookRequestDTO.getQuantity());
    updatedBook.setNumberOfPages(bookRequestDTO.getNumberOfPages());
    updatedBook.setGenre(bookRequestDTO.getGenre());

    return mapper.toBookResponseDTO(bookRepository.save(updatedBook));
  }

  @Transactional
  public void updateBookQuantity(UUID id, int change) {
    Book book = bookRepository.findById(id)
        .orElseThrow(() -> new BookNotFoundException(id));

    int newQuantity = book.getAvailableQuantity() + change;

    if (newQuantity < 0)
      throw new NotEnoughBooksAvailableException();

    book.setAvailableQuantity(newQuantity);
    bookRepository.save(book);
  }

  @Transactional
  public void deleteBook(UUID id) {
    Book book = bookRepository.findById(id)
        .orElseThrow(() -> new BookNotFoundException(id));

    bookRepository.delete(book);
  }
}
