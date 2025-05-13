package com.ilhanozkan.libraryManagementSystem.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ilhanozkan.libraryManagementSystem.model.dto.request.BookRequestDTO;
import com.ilhanozkan.libraryManagementSystem.model.entity.Book;
import com.ilhanozkan.libraryManagementSystem.model.entity.User;
import com.ilhanozkan.libraryManagementSystem.model.entity.Borrowing;
import com.ilhanozkan.libraryManagementSystem.model.enums.BookGenre;
import com.ilhanozkan.libraryManagementSystem.model.enums.UserRole;
import com.ilhanozkan.libraryManagementSystem.model.enums.UserStatus;
import com.ilhanozkan.libraryManagementSystem.repository.BookRepository;
import com.ilhanozkan.libraryManagementSystem.repository.UserRepository;
import com.ilhanozkan.libraryManagementSystem.repository.BorrowingRepository;
import com.ilhanozkan.libraryManagementSystem.security.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.UUID;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class BookControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BorrowingRepository borrowingRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private JwtService jwtService;

    private User testLibrarian;
    private User testPatron;
    private Book testBook;
    private String librarianToken;
    private String patronToken;

    @BeforeEach
    void setUp() {
        // Clean up repositories
        borrowingRepository.deleteAll();
        bookRepository.deleteAll();
        userRepository.deleteAll();

        // Create test users
        testLibrarian = User.builder()
                .username("librarian")
                .email("librarian@test.com")
                .password("password")
                .name("Test")
                .surname("Librarian")
                .role(UserRole.LIBRARIAN)
                .status(UserStatus.ACTIVE)
                .build();
        userRepository.save(testLibrarian);

        testPatron = User.builder()
                .username("patron")
                .email("patron@test.com")
                .password("password")
                .name("Test")
                .surname("Patron")
                .role(UserRole.PATRON)
                .status(UserStatus.ACTIVE)
                .build();
        userRepository.save(testPatron);

        // Generate JWT tokens
        librarianToken = jwtService.generateToken(testLibrarian.getUsername());
        patronToken = jwtService.generateToken(testPatron.getUsername());

        // Create test book
        testBook = Book.builder()
                .name("Test Book")
                .isbn("9781234567890")
                .author("Test Author")
                .publisher("Test Publisher")
                .numberOfPages(200)
                .quantity(10)
                .availableQuantity(10)
                .genre(BookGenre.SCIENCE_FICTION)
                .build();
        bookRepository.save(testBook);
    }

    @Test
    public void shouldGetAllBooks() throws Exception {
        // When
        ResultActions response = mockMvc.perform(get("/api/books")
                .contentType(MediaType.APPLICATION_JSON));

        // Then
        response.andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].name", is("Test Book")))
                .andExpect(jsonPath("$.content[0].isbn", is("9781234567890")))
                .andDo(print());
    }

    @Test
    public void shouldGetBookById() throws Exception {
        // When
        ResultActions response = mockMvc.perform(get("/api/books/{id}", testBook.getId())
                .contentType(MediaType.APPLICATION_JSON));

        // Then
        response.andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("Test Book")))
                .andExpect(jsonPath("$.isbn", is("9781234567890")))
                .andDo(print());
    }

    @Test
    public void shouldGetBookByIsbn() throws Exception {
        // When
        ResultActions response = mockMvc.perform(get("/api/books/isbn/{isbn}", "9781234567890")
                .contentType(MediaType.APPLICATION_JSON));

        // Then
        response.andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("Test Book")))
                .andExpect(jsonPath("$.author", is("Test Author")))
                .andDo(print());
    }

    @Test
    public void shouldCreateBookWhenLibrarian() throws Exception {
        // Given
        BookRequestDTO bookRequestDTO = new BookRequestDTO();
        bookRequestDTO.setName("New Book");
        bookRequestDTO.setIsbn("9789876543210");
        bookRequestDTO.setAuthor("New Author");
        bookRequestDTO.setPublisher("New Publisher");
        bookRequestDTO.setNumberOfPages(250);
        bookRequestDTO.setQuantity(15);
        bookRequestDTO.setGenre(BookGenre.FANTASY);

        // When
        ResultActions response = mockMvc.perform(post("/api/books")
                .header("Authorization", "Bearer " + librarianToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(bookRequestDTO)));

        // Then
        response.andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("New Book")))
                .andExpect(jsonPath("$.isbn", is("9789876543210")))
                .andExpect(jsonPath("$.author", is("New Author")))
                .andDo(print());
    }

    @Test
    public void shouldNotCreateBookWhenPatron() throws Exception {
        // Given
        BookRequestDTO bookRequestDTO = new BookRequestDTO();
        bookRequestDTO.setName("New Book");
        bookRequestDTO.setIsbn("9789876543210");
        bookRequestDTO.setAuthor("New Author");
        bookRequestDTO.setPublisher("New Publisher");
        bookRequestDTO.setNumberOfPages(250);
        bookRequestDTO.setQuantity(15);
        bookRequestDTO.setGenre(BookGenre.FANTASY);

        // When
        ResultActions response = mockMvc.perform(post("/api/books")
                .header("Authorization", "Bearer " + patronToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(bookRequestDTO)));

        // Then
        response.andExpect(status().isForbidden())
                .andDo(print());
    }

    @Test
    public void shouldUpdateBookWhenLibrarian() throws Exception {
        // Given
        BookRequestDTO bookRequestDTO = new BookRequestDTO();
        bookRequestDTO.setName("Updated Book");
        bookRequestDTO.setIsbn("9781234567890");
        bookRequestDTO.setAuthor("Updated Author");
        bookRequestDTO.setPublisher("Updated Publisher");
        bookRequestDTO.setNumberOfPages(300);
        bookRequestDTO.setQuantity(20);
        bookRequestDTO.setGenre(BookGenre.SCIENCE);

        // When
        ResultActions response = mockMvc.perform(put("/api/books/{id}", testBook.getId())
                .header("Authorization", "Bearer " + librarianToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(bookRequestDTO)));

        // Then
        response.andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("Updated Book")))
                .andExpect(jsonPath("$.author", is("Updated Author")))
                .andExpect(jsonPath("$.genre", is("SCIENCE")))
                .andDo(print());
    }

    @Test
    public void shouldDeleteBookWhenLibrarian() throws Exception {
        // When
        ResultActions response = mockMvc.perform(delete("/api/books/{id}", testBook.getId())
                .header("Authorization", "Bearer " + librarianToken));

        // Then
        response.andExpect(status().isOk())
                .andDo(print());

        // Verify book was deleted
        mockMvc.perform(get("/api/books/{id}", testBook.getId()))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void shouldNotDeleteBookWhenPatron() throws Exception {
        // When
        ResultActions response = mockMvc.perform(delete("/api/books/{id}", testBook.getId())
                .header("Authorization", "Bearer " + patronToken));

        // Then
        response.andExpect(status().isForbidden())
                .andDo(print());

        // Verify book was not deleted
        mockMvc.perform(get("/api/books/{id}", testBook.getId()))
                .andExpect(status().isOk());
    }

    @Test
    public void shouldSearchBooks() throws Exception {
        // Add another book for search testing
        Book anotherBook = Book.builder()
                .name("The Hobbit")
                .isbn("9789876543210")
                .author("J.R.R. Tolkien")
                .publisher("Allen & Unwin")
                .numberOfPages(310)
                .quantity(3)
                .availableQuantity(3)
                .genre(BookGenre.FANTASY)
                .build();
        bookRepository.save(anotherBook);

        // Search by title
        mockMvc.perform(get("/api/books/search")
                .param("title", "Hobbit")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].name", is("The Hobbit")))
                .andDo(print());

        // Search by author
        mockMvc.perform(get("/api/books/search")
                .param("author", "Test")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].name", is("Test Book")))
                .andDo(print());
    }
} 