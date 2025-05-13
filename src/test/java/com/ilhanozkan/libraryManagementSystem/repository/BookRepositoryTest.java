package com.ilhanozkan.libraryManagementSystem.repository;

import com.ilhanozkan.libraryManagementSystem.model.entity.Book;
import com.ilhanozkan.libraryManagementSystem.model.enums.BookGenre;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
public class BookRepositoryTest {

    @Autowired
    private BookRepository bookRepository;

    @Test
    public void shouldSaveBook() {
        // Arrange
        Book book = Book.builder()
                .name("Test Book")
                .isbn("9781234567890")
                .author("Test Author")
                .publisher("Test Publisher")
                .numberOfPages(200)
                .quantity(10)
                .availableQuantity(10)
                .genre(BookGenre.SCIENCE_FICTION)
                .build();

        // Act
        Book savedBook = bookRepository.save(book);

        // Assert
        assertThat(savedBook).isNotNull();
        assertThat(savedBook.getId()).isNotNull();
        assertThat(savedBook.getName()).isEqualTo("Test Book");
        assertThat(savedBook.getIsbn()).isEqualTo("9781234567890");
    }

    @Test
    public void shouldFindBookById() {
        // Arrange
        Book book = Book.builder()
                .name("Test Book")
                .isbn("9781234567890")
                .author("Test Author")
                .publisher("Test Publisher")
                .numberOfPages(200)
                .quantity(10)
                .availableQuantity(10)
                .genre(BookGenre.SCIENCE_FICTION)
                .build();
        Book savedBook = bookRepository.save(book);

        // Act
        Optional<Book> foundBook = bookRepository.findById(savedBook.getId());

        // Assert
        assertThat(foundBook).isPresent();
        assertThat(foundBook.get().getName()).isEqualTo("Test Book");
    }

    @Test
    public void shouldFindBookByIsbn() {
        // Arrange
        Book book = Book.builder()
                .name("Test Book")
                .isbn("9781234567890")
                .author("Test Author")
                .publisher("Test Publisher")
                .numberOfPages(200)
                .quantity(10)
                .availableQuantity(10)
                .genre(BookGenre.SCIENCE_FICTION)
                .build();
        bookRepository.save(book);

        // Act
        Book foundBook = bookRepository.findByIsbn("9781234567890");

        // Assert
        assertThat(foundBook).isNotNull();
        assertThat(foundBook.getName()).isEqualTo("Test Book");
    }

    @Test
    public void shouldSearchBooks() {
        // Arrange
        Book book1 = Book.builder()
                .name("Harry Potter")
                .isbn("9781234567890")
                .author("J.K. Rowling")
                .publisher("Bloomsbury")
                .numberOfPages(300)
                .quantity(5)
                .availableQuantity(5)
                .genre(BookGenre.FANTASY)
                .build();

        Book book2 = Book.builder()
                .name("The Hobbit")
                .isbn("9789876543210")
                .author("J.R.R. Tolkien")
                .publisher("Allen & Unwin")
                .numberOfPages(310)
                .quantity(3)
                .availableQuantity(3)
                .genre(BookGenre.FANTASY)
                .build();

        bookRepository.saveAll(List.of(book1, book2));

        // Act
        List<Book> foundBooks = bookRepository.searchBooks("Harry", null, null, null);
        List<Book> foundByAuthor = bookRepository.searchBooks(null, "Tolkien", null, null);
        List<Book> foundByGenre = bookRepository.searchBooks(null, null, null, String.valueOf(BookGenre.FANTASY.ordinal()));

        // Assert
        assertThat(foundBooks).hasSize(1);
        assertThat(foundBooks.get(0).getName()).isEqualTo("Harry Potter");
        
        assertThat(foundByAuthor).hasSize(1);
        assertThat(foundByAuthor.get(0).getName()).isEqualTo("The Hobbit");
        
        assertThat(foundByGenre).hasSize(2);
    }

    @Test
    public void shouldDeleteBook() {
        // Arrange
        Book book = Book.builder()
                .name("Test Book")
                .isbn("9781234567890")
                .author("Test Author")
                .publisher("Test Publisher")
                .numberOfPages(200)
                .quantity(10)
                .availableQuantity(10)
                .genre(BookGenre.SCIENCE_FICTION)
                .build();
        Book savedBook = bookRepository.save(book);

        // Act
        bookRepository.deleteById(savedBook.getId());
        Optional<Book> deletedBook = bookRepository.findById(savedBook.getId());

        // Assert
        assertThat(deletedBook).isEmpty();
    }
} 