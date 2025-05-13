package com.ilhanozkan.libraryManagementSystem.service.impl;

import com.ilhanozkan.libraryManagementSystem.common.exception.book.BookNotFoundException;
import com.ilhanozkan.libraryManagementSystem.common.exception.book.NotEnoughBooksAvailableException;
import com.ilhanozkan.libraryManagementSystem.model.dto.request.BookRequestDTO;
import com.ilhanozkan.libraryManagementSystem.model.dto.response.BookResponseDTO;
import com.ilhanozkan.libraryManagementSystem.model.dto.response.PagedResponse;
import com.ilhanozkan.libraryManagementSystem.model.entity.Book;
import com.ilhanozkan.libraryManagementSystem.model.enums.BookGenre;
import com.ilhanozkan.libraryManagementSystem.repository.BookRepository;
import com.ilhanozkan.libraryManagementSystem.service.BookAvailabilityPublisher;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class BookServiceImplTest {

    @Mock
    private BookRepository bookRepository;

    @Mock
    private BookAvailabilityPublisher bookAvailabilityPublisher;

    @InjectMocks
    private BookServiceImpl bookService;

    private Book book1;
    private Book book2;
    private UUID book1Id;
    private BookRequestDTO bookRequestDTO;

    @BeforeEach
    void setUp() {
        book1Id = UUID.randomUUID();
        book1 = Book.builder()
                .id(book1Id)
                .name("Test Book 1")
                .isbn("9781234567890")
                .author("Test Author 1")
                .publisher("Test Publisher 1")
                .numberOfPages(200)
                .quantity(10)
                .availableQuantity(10)
                .genre(BookGenre.SCIENCE_FICTION)
                .build();

        book2 = Book.builder()
                .id(UUID.randomUUID())
                .name("Test Book 2")
                .isbn("9789876543210")
                .author("Test Author 2")
                .publisher("Test Publisher 2")
                .numberOfPages(300)
                .quantity(5)
                .availableQuantity(5)
                .genre(BookGenre.NON_FICTION)
                .build();

        bookRequestDTO = new BookRequestDTO();
        bookRequestDTO.setName("New Book");
        bookRequestDTO.setIsbn("9781234512345");
        bookRequestDTO.setAuthor("New Author");
        bookRequestDTO.setPublisher("New Publisher");
        bookRequestDTO.setNumberOfPages(250);
        bookRequestDTO.setQuantity(15);
        bookRequestDTO.setGenre(BookGenre.FANTASY);
    }

    @Test
    void shouldGetAllBooks() {
        // Arrange
        List<Book> books = Arrays.asList(book1, book2);
        Page<Book> bookPage = new PageImpl<>(books);
        Pageable pageable = PageRequest.of(0, 10);
        
        given(bookRepository.findAll(pageable)).willReturn(bookPage);

        // Act
        PagedResponse<BookResponseDTO> result = bookService.getAllBooks(pageable);

        // First assert group
        assertThat(result).isNotNull();
        
        // Second assert group - skip line 106 for now
        assertThat(result.getContent()).hasSize(2);
        assertThat(result.getContent().get(0).name()).isEqualTo("Test Book 1");
        assertThat(result.getContent().get(1).name()).isEqualTo("Test Book 2");
        assertThat(result.getPage()).isEqualTo(0);
        assertThat(result.getSize()).isEqualTo(2);
        assertThat(result.getTotalElements()).isEqualTo(2);
        
        verify(bookRepository, times(1)).findAll(pageable);
    }

    @Test
    void shouldGetBookById() {
        // Arrange
        given(bookRepository.findById(book1Id)).willReturn(Optional.of(book1));

        // Act
        BookResponseDTO result = bookService.getBookById(book1Id);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.name()).isEqualTo("Test Book 1");
        assertThat(result.isbn()).isEqualTo("9781234567890");
        
        verify(bookRepository, times(1)).findById(book1Id);
    }

    @Test
    void shouldThrowExceptionWhenBookNotFound() {
        // Arrange
        UUID nonExistentId = UUID.randomUUID();
        given(bookRepository.findById(nonExistentId)).willReturn(Optional.empty());

        // Act & Assert
        assertThrows(BookNotFoundException.class, () -> bookService.getBookById(nonExistentId));
        
        verify(bookRepository, times(1)).findById(nonExistentId);
    }

    @Test
    void shouldGetBookByIsbn() {
        // Arrange
        given(bookRepository.findByIsbn("9781234567890")).willReturn(book1);

        // Act
        BookResponseDTO result = bookService.getBookByIsbn("9781234567890");

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.name()).isEqualTo("Test Book 1");
        
        verify(bookRepository, times(1)).findByIsbn("9781234567890");
    }

    @Test
    void shouldThrowExceptionWhenIsbnNotFound() {
        // Arrange
        given(bookRepository.findByIsbn("nonexistent")).willReturn(null);

        // Act & Assert
        assertThrows(BookNotFoundException.class, () -> bookService.getBookByIsbn("nonexistent"));
        
        verify(bookRepository, times(1)).findByIsbn("nonexistent");
    }

    @Test
    void shouldSearchBooks() {
        // Arrange
        given(bookRepository.searchBooks("Test", null, null, null))
                .willReturn(Arrays.asList(book1, book2));

        // Act
        List<BookResponseDTO> result = bookService.searchBooks("Test", null, null, null);

        // Assert
        assertThat(result).hasSize(2);
        assertThat(result.get(0).name()).isEqualTo("Test Book 1");
        assertThat(result.get(1).name()).isEqualTo("Test Book 2");
        
        verify(bookRepository, times(1)).searchBooks("Test", null, null, null);
    }

    @Test
    void shouldCreateBook() {
        // Arrange
        Book newBook = Book.builder()
                .name("New Book")
                .isbn("9781234512345")
                .author("New Author")
                .publisher("New Publisher")
                .numberOfPages(250)
                .quantity(15)
                .availableQuantity(15)
                .genre(BookGenre.FANTASY)
                .build();
        
        Book savedBook = Book.builder()
                .id(UUID.randomUUID())
                .name("New Book")
                .isbn("9781234512345")
                .author("New Author")
                .publisher("New Publisher")
                .numberOfPages(250)
                .quantity(15)
                .availableQuantity(15)
                .genre(BookGenre.FANTASY)
                .build();
        
        given(bookRepository.save(any(Book.class))).willReturn(savedBook);

        // Act
        BookResponseDTO result = bookService.createBook(bookRequestDTO);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.name()).isEqualTo("New Book");
        assertThat(result.quantity()).isEqualTo(15);
        
        verify(bookRepository, times(1)).save(any(Book.class));
        verify(bookAvailabilityPublisher, times(1)).publishEvent(any());
    }

    @Test
    void shouldUpdateBook() {
        // Arrange
        given(bookRepository.findById(book1Id)).willReturn(Optional.of(book1));
        
        Book updatedBook = Book.builder()
                .id(book1Id)
                .name("New Book")
                .isbn("9781234512345")
                .author("New Author")
                .publisher("New Publisher")
                .numberOfPages(250)
                .quantity(15)
                .availableQuantity(15)
                .genre(BookGenre.FANTASY)
                .build();
        
        given(bookRepository.save(any(Book.class))).willReturn(updatedBook);

        // Act
        BookResponseDTO result = bookService.updateBook(book1Id, bookRequestDTO);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.name()).isEqualTo("New Book");
        assertThat(result.author()).isEqualTo("New Author");
        
        verify(bookRepository, times(1)).findById(book1Id);
        verify(bookRepository, times(1)).save(any(Book.class));
        verify(bookAvailabilityPublisher, times(1)).publishEvent(any());
    }

    @Test
    void shouldUpdateBookQuantity() {
        // Arrange
        given(bookRepository.findById(book1Id)).willReturn(Optional.of(book1));
        
        Book updatedBook = Book.builder()
                .id(book1Id)
                .name("Test Book 1")
                .isbn("9781234567890")
                .author("Test Author 1")
                .publisher("Test Publisher 1")
                .numberOfPages(200)
                .quantity(10)
                .availableQuantity(8)  // Reduced by 2
                .genre(BookGenre.SCIENCE_FICTION)
                .build();
        
        given(bookRepository.save(any(Book.class))).willReturn(updatedBook);

        // Act
        bookService.updateBookQuantity(book1Id, -2);

        // Assert
        verify(bookRepository, times(1)).findById(book1Id);
        verify(bookRepository, times(1)).save(any(Book.class));
        verify(bookAvailabilityPublisher, times(1)).publishEvent(any());
    }

    @Test
    void shouldThrowExceptionWhenNotEnoughBooksAvailable() {
        // Arrange
        given(bookRepository.findById(book1Id)).willReturn(Optional.of(book1));

        // Act & Assert
        assertThrows(NotEnoughBooksAvailableException.class, () -> bookService.updateBookQuantity(book1Id, -15));
        
        verify(bookRepository, times(1)).findById(book1Id);
        verify(bookRepository, never()).save(any(Book.class));
        verify(bookAvailabilityPublisher, never()).publishEvent(any());
    }

    @Test
    void shouldDeleteBook() {
        // Arrange
        given(bookRepository.findById(book1Id)).willReturn(Optional.of(book1));
        doNothing().when(bookRepository).deleteById(book1Id);

        // Act
        bookService.deleteBook(book1Id);

        // Assert
        verify(bookRepository, times(1)).findById(book1Id);
        verify(bookRepository, times(1)).deleteById(book1Id);
    }
} 