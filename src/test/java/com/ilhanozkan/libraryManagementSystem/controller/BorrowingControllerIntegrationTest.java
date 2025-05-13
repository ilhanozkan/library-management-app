package com.ilhanozkan.libraryManagementSystem.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ilhanozkan.libraryManagementSystem.model.entity.Book;
import com.ilhanozkan.libraryManagementSystem.model.entity.Borrowing;
import com.ilhanozkan.libraryManagementSystem.model.entity.User;
import com.ilhanozkan.libraryManagementSystem.model.enums.BookGenre;
import com.ilhanozkan.libraryManagementSystem.model.enums.UserRole;
import com.ilhanozkan.libraryManagementSystem.model.enums.UserStatus;
import com.ilhanozkan.libraryManagementSystem.repository.BookRepository;
import com.ilhanozkan.libraryManagementSystem.repository.BorrowingRepository;
import com.ilhanozkan.libraryManagementSystem.repository.UserRepository;
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

import java.time.LocalDateTime;
import java.util.UUID;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.hasSize;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class BorrowingControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private BorrowingRepository borrowingRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private JwtService jwtService;

    private User librarian;
    private User patron;
    private Book testBook;
    private Borrowing testBorrowing;
    private String librarianToken;
    private String patronToken;

    @BeforeEach
    void setUp() {
        // Clean up repositories
        borrowingRepository.deleteAll();
        bookRepository.deleteAll();
        userRepository.deleteAll();

        // Create test users
        librarian = User.builder()
                .username("librarian")
                .email("librarian@test.com")
                .password("password")
                .name("Librarian")
                .surname("User")
                .role(UserRole.LIBRARIAN)
                .status(UserStatus.ACTIVE)
                .build();
        userRepository.save(librarian);

        patron = User.builder()
                .username("patron")
                .email("patron@test.com")
                .password("password")
                .name("Patron")
                .surname("User")
                .role(UserRole.PATRON)
                .status(UserStatus.ACTIVE)
                .build();
        userRepository.save(patron);

        // Generate JWT tokens
        librarianToken = jwtService.generateToken(librarian.getUsername());
        patronToken = jwtService.generateToken(patron.getUsername());

        // Create test book
        testBook = Book.builder()
                .name("Test Book")
                .isbn("9781234567890")
                .author("Test Author")
                .publisher("Test Publisher")
                .numberOfPages(200)
                .quantity(11)
                .availableQuantity(10)
                .genre(BookGenre.SCIENCE_FICTION)
                .build();
        bookRepository.save(testBook);

        // Create test borrowing
        LocalDateTime now = LocalDateTime.now();
        testBorrowing = Borrowing.builder()
                .book(testBook)
                .user(patron)
                .borrowDate(now)
                .dueDate(now.plusDays(14))
                .returned(false)
                .updatedAt(now)
                .build();
        borrowingRepository.save(testBorrowing);
    }

    @Test
    public void shouldGetAllBorrowingsWhenLibrarian() throws Exception {
        // When
        ResultActions response = mockMvc.perform(get("/borrowings")
                .header("Authorization", "Bearer " + librarianToken)
                .contentType(MediaType.APPLICATION_JSON));

        // Then
        response.andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id").exists())
                .andDo(print());
    }

    @Test
    public void shouldNotGetAllBorrowingsWhenPatron() throws Exception {
        // When
        ResultActions response = mockMvc.perform(get("/borrowings")
                .header("Authorization", "Bearer " + patronToken)
                .contentType(MediaType.APPLICATION_JSON));

        // Then
        response.andExpect(status().isForbidden())
                .andDo(print());
    }

    @Test
    public void shouldGetBorrowingsByUserIdWhenLibrarian() throws Exception {
        // When
        ResultActions response = mockMvc.perform(get("/borrowings/user/{userId}", patron.getId())
                .header("Authorization", "Bearer " + librarianToken)
                .contentType(MediaType.APPLICATION_JSON));

        // Then
        response.andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].bookName", is("Test Book")))
                .andDo(print());
    }

    @Test
    public void shouldGetMyBorrowingHistory() throws Exception {
        // When
        ResultActions response = mockMvc.perform(get("/borrowings/my-history")
                .header("Authorization", "Bearer " + patronToken)
                .contentType(MediaType.APPLICATION_JSON));

        // Then
        response.andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].bookName", is("Test Book")))
                .andDo(print());
    }

    @Test
    public void shouldCreateBorrowingWhenPatron() throws Exception {
        // Given: Add another book
        Book anotherBook = Book.builder()
                .name("Another Book")
                .isbn("9789876543210")
                .author("Another Author")
                .publisher("Another Publisher")
                .numberOfPages(300)
                .quantity(5)
                .availableQuantity(5)
                .genre(BookGenre.NON_FICTION)
                .build();
        bookRepository.save(anotherBook);

        // When
        ResultActions response = mockMvc.perform(post("/borrowings")
                .header("Authorization", "Bearer " + patronToken)
                .param("bookId", anotherBook.getId().toString())
                .contentType(MediaType.APPLICATION_JSON));

        // Then
        response.andExpect(status().isOk())
                .andExpect(jsonPath("$.bookName", is("Another Book")))
                .andExpect(jsonPath("$.userFullName", is("Patron User")))
                .andExpect(jsonPath("$.returned", is(false)))
                .andDo(print());

        // Verify the book's available quantity was decreased
        Book updatedBook = bookRepository.findById(anotherBook.getId()).orElseThrow();
        assert updatedBook.getAvailableQuantity() == 4 : "Available quantity should be 4 but was " + updatedBook.getAvailableQuantity();
    }

    @Test
    public void shouldCreateBorrowingWhenLibrarian() throws Exception {
        // Given: Add another book
        Book anotherBook = Book.builder()
                .name("Another Book")
                .isbn("9789876543210")
                .author("Another Author")
                .publisher("Another Publisher")
                .numberOfPages(300)
                .quantity(5)
                .availableQuantity(5)
                .genre(BookGenre.NON_FICTION)
                .build();
        bookRepository.save(anotherBook);

        // When
        ResultActions response = mockMvc.perform(post("/borrowings/librarian")
                .header("Authorization", "Bearer " + librarianToken)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("bookId", anotherBook.getId().toString())
                .param("userId", patron.getId().toString()));

        // Then
        response.andExpect(status().isOk())
                .andExpect(jsonPath("$.bookName", is("Another Book")))
                .andExpect(jsonPath("$.userFullName", is("Patron User")))
                .andExpect(jsonPath("$.returned", is(false)))
                .andDo(print());

        // Verify the book's available quantity was decreased
        Book updatedBook = bookRepository.findById(anotherBook.getId()).orElseThrow();
        assert updatedBook.getAvailableQuantity() == 4 : "Available quantity should be 4 but was " + updatedBook.getAvailableQuantity();
    }

    @Test
    public void shouldReturnBook() throws Exception {
        // When
        ResultActions response = mockMvc.perform(put("/borrowings/{id}/return", testBorrowing.getId())
                .header("Authorization", "Bearer " + patronToken)
                .contentType(MediaType.APPLICATION_JSON));

        // Then
        response.andExpect(status().isOk())
                .andExpect(jsonPath("$.returned", is(true)))
                .andExpect(jsonPath("$.returnDate").exists())
                .andDo(print());

        // Explicitly refresh the book entity from the database
        Book updatedBook = bookRepository.findById(testBook.getId()).orElseThrow();
        assertThat(updatedBook.getAvailableQuantity()).isEqualTo(11); // Expecting 10 + 1 = 11
    }

    @Test
    public void shouldDeleteBorrowingWhenLibrarian() throws Exception {
        // When
        ResultActions response = mockMvc.perform(delete("/borrowings/{id}", testBorrowing.getId())
                .header("Authorization", "Bearer " + librarianToken)
                .contentType(MediaType.APPLICATION_JSON));

        // Then
        response.andExpect(status().isOk())
                .andDo(print());

        // Verify borrowing was deleted
        assert borrowingRepository.findById(testBorrowing.getId()).isEmpty();

        // Need to explicitly refresh the book entity from the database to see the updated quantity
        Book updatedBook = bookRepository.findById(testBook.getId()).orElseThrow();
        assertThat(updatedBook.getAvailableQuantity()).isEqualTo(11); // Should be 10 + 1 = 11
    }

    @Test
    public void shouldNotDeleteBorrowingWhenPatron() throws Exception {
        // When
        ResultActions response = mockMvc.perform(delete("/borrowings/{id}", testBorrowing.getId())
                .header("Authorization", "Bearer " + patronToken)
                .contentType(MediaType.APPLICATION_JSON));

        // Then
        response.andExpect(status().isForbidden())
                .andDo(print());

        // Verify borrowing was not deleted
        assert borrowingRepository.findById(testBorrowing.getId()).isPresent();
    }

    @Test
    public void shouldGenerateOverdueReport() throws Exception {
        // Create an overdue borrowing
        LocalDateTime now = LocalDateTime.now();
        Borrowing overdueBorrowing = Borrowing.builder()
                .book(testBook)
                .user(patron)
                .borrowDate(now.minusDays(15))
                .dueDate(now.minusDays(1))  // Overdue
                .returned(false)
                .updatedAt(now)
                .build();
        borrowingRepository.save(overdueBorrowing);

        // When
        ResultActions response = mockMvc.perform(get("/borrowings/overdue-report")
                .header("Authorization", "Bearer " + librarianToken)
                .contentType(MediaType.APPLICATION_JSON));

        // Then
        response.andExpect(status().isOk())
                .andExpect(content().contentType("text/plain;charset=UTF-8"))
                .andDo(print());
    }
}