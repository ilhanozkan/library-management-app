package com.ilhanozkan.libraryManagementSystem.config;

import com.ilhanozkan.libraryManagementSystem.model.entity.Book;
import com.ilhanozkan.libraryManagementSystem.model.entity.Borrowing;
import com.ilhanozkan.libraryManagementSystem.model.entity.User;
import com.ilhanozkan.libraryManagementSystem.model.enums.BookGenre;
import com.ilhanozkan.libraryManagementSystem.model.enums.UserRole;
import com.ilhanozkan.libraryManagementSystem.model.enums.UserStatus;
import com.ilhanozkan.libraryManagementSystem.repository.BookRepository;
import com.ilhanozkan.libraryManagementSystem.repository.BorrowingRepository;
import com.ilhanozkan.libraryManagementSystem.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final BookRepository bookRepository;
    private final BorrowingRepository borrowingRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        // Only initialize if the repositories are empty
        if (userRepository.count() == 0) {
            initializeUsers();
        }

        if (bookRepository.count() == 0) {
            initializeBooks();
        }
        
        if (borrowingRepository.count() == 0) {
            initializeBorrowings();
        }
    }

    private void initializeUsers() {
        // Create librarian
        User librarian = User.builder()
                .username("librarian")
                .email("librarian@library.com")
                .password(passwordEncoder.encode("password"))
                .name("Admin")
                .surname("Librarian")
                .role(UserRole.LIBRARIAN)
                .status(UserStatus.ACTIVE)
                .build();
        userRepository.save(librarian);

        // Create patron
        User patron = User.builder()
                .username("patron")
                .email("patron@example.com")
                .password(passwordEncoder.encode("password"))
                .name("Regular")
                .surname("User")
                .role(UserRole.PATRON)
                .status(UserStatus.ACTIVE)
                .build();
        userRepository.save(patron);
    }

    private void initializeBooks() {
        // Create some sample books
        Book book1 = Book.builder()
                .name("To Kill a Mockingbird")
                .isbn("9780061120084")
                .author("Harper Lee")
                .publisher("HarperCollins")
                .numberOfPages(281)
                .quantity(5)
                .availableQuantity(5)
                .genre(BookGenre.CLASSIC)
                .build();
        bookRepository.save(book1);

        Book book2 = Book.builder()
                .name("1984")
                .isbn("9780451524935")
                .author("George Orwell")
                .publisher("Signet Classics")
                .numberOfPages(328)
                .quantity(3)
                .availableQuantity(3)
                .genre(BookGenre.DYSTOPIAN)
                .build();
        bookRepository.save(book2);

        Book book3 = Book.builder()
                .name("The Great Gatsby")
                .isbn("9780743273565")
                .author("F. Scott Fitzgerald")
                .publisher("Scribner")
                .numberOfPages(180)
                .quantity(4)
                .availableQuantity(4)
                .genre(BookGenre.CLASSIC)
                .build();
        bookRepository.save(book3);

        Book book4 = Book.builder()
                .name("Harry Potter and the Sorcerer's Stone")
                .isbn("9780590353427")
                .author("J.K. Rowling")
                .publisher("Scholastic")
                .numberOfPages(309)
                .quantity(7)
                .availableQuantity(7)
                .genre(BookGenre.FANTASY)
                .build();
        bookRepository.save(book4);

        Book book5 = Book.builder()
                .name("The Hobbit")
                .isbn("9780547928227")
                .author("J.R.R. Tolkien")
                .publisher("Houghton Mifflin Harcourt")
                .numberOfPages(304)
                .quantity(6)
                .availableQuantity(6)
                .genre(BookGenre.FANTASY)
                .build();
        bookRepository.save(book5);
    }
    
    private void initializeBorrowings() {
        User patron = userRepository.findByUsername("patron");
        if (patron == null) return;
        
        List<Book> books = bookRepository.findAll();
        if (books.isEmpty()) return;
        
        // A currently borrowed book
        Book book1 = books.get(0);
        Borrowing currentBorrowing = Borrowing.builder()
                .book(book1)
                .user(patron)
                .borrowDate(LocalDateTime.now().minusDays(7))
                .dueDate(LocalDateTime.now().plusDays(7))
                .returned(false)
                .updatedAt(LocalDateTime.now())
                .build();
        borrowingRepository.save(currentBorrowing);
        
        // Update available quantity
        book1.setAvailableQuantity(book1.getAvailableQuantity() - 1);
        bookRepository.save(book1);
        
        // A returned book
        if (books.size() > 1) {
            Book book2 = books.get(1);
            Borrowing returnedBorrowing = Borrowing.builder()
                    .book(book2)
                    .user(patron)
                    .borrowDate(LocalDateTime.now().minusDays(30))
                    .dueDate(LocalDateTime.now().minusDays(16))
                    .returnDate(LocalDateTime.now().minusDays(18))
                    .returned(true)
                    .updatedAt(LocalDateTime.now().minusDays(18))
                    .build();
            borrowingRepository.save(returnedBorrowing);
        }
        
        // An overdue book
        if (books.size() > 2) {
            Book book3 = books.get(2);
            Borrowing overdueBorrowing = Borrowing.builder()
                    .book(book3)
                    .user(patron)
                    .borrowDate(LocalDateTime.now().minusDays(20))
                    .dueDate(LocalDateTime.now().minusDays(6))
                    .returned(false)
                    .updatedAt(LocalDateTime.now())
                    .build();
            borrowingRepository.save(overdueBorrowing);
            
            // Update available quantity
            book3.setAvailableQuantity(book3.getAvailableQuantity() - 1);
            bookRepository.save(book3);
        }
    }
} 