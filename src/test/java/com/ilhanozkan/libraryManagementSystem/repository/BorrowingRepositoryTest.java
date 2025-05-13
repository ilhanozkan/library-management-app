package com.ilhanozkan.libraryManagementSystem.repository;

import com.ilhanozkan.libraryManagementSystem.model.entity.Book;
import com.ilhanozkan.libraryManagementSystem.model.entity.Borrowing;
import com.ilhanozkan.libraryManagementSystem.model.entity.User;
import com.ilhanozkan.libraryManagementSystem.model.enums.BookGenre;
import com.ilhanozkan.libraryManagementSystem.model.enums.UserRole;
import com.ilhanozkan.libraryManagementSystem.model.enums.UserStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
public class BorrowingRepositoryTest {

    @Autowired
    private BorrowingRepository borrowingRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BookRepository bookRepository;

    private User testUser;
    private Book testBook;

    @BeforeEach
    void setUp() {
        // Create a test user
        testUser = User.builder()
                .username("testuser")
                .email("test@example.com")
                .password("password")
                .name("Test")
                .surname("User")
                .role(UserRole.PATRON)
                .status(UserStatus.ACTIVE)
                .build();
        userRepository.save(testUser);

        // Create a test book
        testBook = Book.builder()
                .name("Test Book")
                .isbn("9781234567890")
                .author("Test Author")
                .publisher("Test Publisher")
                .numberOfPages(200)
                .quantity(10)
                .availableQuantity(9)
                .genre(BookGenre.SCIENCE_FICTION)
                .build();
        bookRepository.save(testBook);
    }

    @Test
    public void shouldSaveBorrowing() {
        // Arrange
        LocalDateTime now = LocalDateTime.now();
        Borrowing borrowing = Borrowing.builder()
                .book(testBook)
                .user(testUser)
                .borrowDate(now)
                .dueDate(now.plusDays(14))
                .returned(false)
                .updatedAt(now)
                .build();

        // Act
        Borrowing savedBorrowing = borrowingRepository.save(borrowing);

        // Assert
        assertThat(savedBorrowing).isNotNull();
        assertThat(savedBorrowing.getId()).isNotNull();
        assertThat(savedBorrowing.getBook().getName()).isEqualTo("Test Book");
        assertThat(savedBorrowing.getUser().getUsername()).isEqualTo("testuser");
        assertThat(savedBorrowing.getReturned()).isFalse();
    }

    @Test
    public void shouldFindBorrowingById() {
        // Arrange
        LocalDateTime now = LocalDateTime.now();
        Borrowing borrowing = Borrowing.builder()
                .book(testBook)
                .user(testUser)
                .borrowDate(now)
                .dueDate(now.plusDays(14))
                .returned(false)
                .updatedAt(now)
                .build();
        Borrowing savedBorrowing = borrowingRepository.save(borrowing);

        // Act
        Optional<Borrowing> foundBorrowing = borrowingRepository.findById(savedBorrowing.getId());

        // Assert
        assertThat(foundBorrowing).isPresent();
        assertThat(foundBorrowing.get().getBook().getName()).isEqualTo("Test Book");
        assertThat(foundBorrowing.get().getUser().getUsername()).isEqualTo("testuser");
    }

    @Test
    public void shouldFindBorrowingsByUserAndNotReturned() {
        // Arrange
        LocalDateTime now = LocalDateTime.now();
        
        Borrowing borrowing1 = Borrowing.builder()
                .book(testBook)
                .user(testUser)
                .borrowDate(now)
                .dueDate(now.plusDays(14))
                .returned(false)
                .updatedAt(now)
                .build();
        
        Book book2 = Book.builder()
                .name("Another Book")
                .isbn("9789876543210")
                .author("Another Author")
                .publisher("Another Publisher")
                .numberOfPages(300)
                .quantity(5)
                .availableQuantity(4)
                .genre(BookGenre.NON_FICTION)
                .build();
        bookRepository.save(book2);
        
        Borrowing borrowing2 = Borrowing.builder()
                .book(book2)
                .user(testUser)
                .borrowDate(now.minusDays(10))
                .dueDate(now.plusDays(4))
                .returned(false)
                .updatedAt(now)
                .build();
        
        Borrowing borrowing3 = Borrowing.builder()
                .book(book2)
                .user(testUser)
                .borrowDate(now.minusDays(20))
                .dueDate(now.minusDays(6))
                .returnDate(now.minusDays(7))
                .returned(true)
                .updatedAt(now)
                .build();
        
        borrowingRepository.saveAll(List.of(borrowing1, borrowing2, borrowing3));

        // Act
        List<Borrowing> activeUserBorrowings = borrowingRepository.findByUserAndReturnedFalse(testUser);
        List<Borrowing> allUserBorrowings = borrowingRepository.findByUser(testUser);

        // Assert
        assertThat(activeUserBorrowings).hasSize(2);
        assertThat(allUserBorrowings).hasSize(3);
    }

    @Test
    public void shouldFindOverdueBooks() {
        // Arrange
        LocalDateTime now = LocalDateTime.now();
        
        Borrowing borrowing1 = Borrowing.builder()
                .book(testBook)
                .user(testUser)
                .borrowDate(now.minusDays(15))
                .dueDate(now.minusDays(1))  // Overdue
                .returned(false)
                .updatedAt(now)
                .build();
        
        Borrowing borrowing2 = Borrowing.builder()
                .book(testBook)
                .user(testUser)
                .borrowDate(now)
                .dueDate(now.plusDays(14))  // Not overdue
                .returned(false)
                .updatedAt(now)
                .build();
        
        Borrowing borrowing3 = Borrowing.builder()
                .book(testBook)
                .user(testUser)
                .borrowDate(now.minusDays(20))
                .dueDate(now.minusDays(6))  // Would be overdue but is returned
                .returnDate(now.minusDays(7))
                .returned(true)
                .updatedAt(now)
                .build();
        
        borrowingRepository.saveAll(List.of(borrowing1, borrowing2, borrowing3));

        // Act
        List<Borrowing> overdueBooks = borrowingRepository.findOverdueBooks();

        // Assert
        assertThat(overdueBooks).hasSize(1);
        assertThat(overdueBooks.get(0).getDueDate()).isBefore(now);
        assertThat(overdueBooks.get(0).getReturned()).isFalse();
    }

    @Test
    public void shouldUpdateBorrowingWhenReturned() {
        // Arrange
        LocalDateTime now = LocalDateTime.now();
        Borrowing borrowing = Borrowing.builder()
                .book(testBook)
                .user(testUser)
                .borrowDate(now.minusDays(5))
                .dueDate(now.plusDays(9))
                .returned(false)
                .updatedAt(now)
                .build();
        Borrowing savedBorrowing = borrowingRepository.save(borrowing);

        // Act
        savedBorrowing.setReturned(true);
        savedBorrowing.setReturnDate(now);
        savedBorrowing.setUpdatedAt(now);
        borrowingRepository.save(savedBorrowing);

        // Assert
        Optional<Borrowing> updatedBorrowing = borrowingRepository.findById(savedBorrowing.getId());
        assertThat(updatedBorrowing).isPresent();
        assertThat(updatedBorrowing.get().getReturned()).isTrue();
        assertThat(updatedBorrowing.get().getReturnDate()).isNotNull();
    }
} 