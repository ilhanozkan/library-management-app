package com.ilhanozkan.libraryManagementSystem.service.impl;

import com.ilhanozkan.libraryManagementSystem.model.dto.request.BorrowingRequestDTO;
import com.ilhanozkan.libraryManagementSystem.model.dto.response.BorrowingResponseDTO;
import com.ilhanozkan.libraryManagementSystem.model.entity.Book;
import com.ilhanozkan.libraryManagementSystem.model.entity.Borrowing;
import com.ilhanozkan.libraryManagementSystem.model.entity.User;
import com.ilhanozkan.libraryManagementSystem.model.enums.UserStatus;
import com.ilhanozkan.libraryManagementSystem.model.exception.BookNotAvailableException;
import com.ilhanozkan.libraryManagementSystem.model.exception.BookNotFoundException;
import com.ilhanozkan.libraryManagementSystem.model.exception.UserIsNotActiveException;
import com.ilhanozkan.libraryManagementSystem.model.exception.UserNotFoundException;
import com.ilhanozkan.libraryManagementSystem.model.mapper.BorrowingResponseDTOMapper;
import com.ilhanozkan.libraryManagementSystem.repository.BookRepository;
import com.ilhanozkan.libraryManagementSystem.repository.BorrowingRepository;
import com.ilhanozkan.libraryManagementSystem.repository.UserRepository;
import com.ilhanozkan.libraryManagementSystem.service.BorrowingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BorrowingServiceImpl implements BorrowingService {
  private final BorrowingRepository borrowingRepository;
  private final BookRepository bookRepository;
  private final UserRepository userRepository;
  private final BorrowingResponseDTOMapper mapper = BorrowingResponseDTOMapper.INSTANCE;

  public List<BorrowingResponseDTO> getBorrowings() {
    try {
      return mapper.toBorrowingResponseDTOList(borrowingRepository.findAll());
    } catch (RuntimeException e) {
      throw new RuntimeException(e.getMessage());
    }
  }

  public ResponseEntity<?> createBorrowing(BorrowingRequestDTO borrowingRequestDTO) {
    try {
      Book book = bookRepository.findById(borrowingRequestDTO.bookId()).orElseThrow(
          () -> new BookNotFoundException(borrowingRequestDTO.bookId())
      );

      User user = userRepository.findById(borrowingRequestDTO.userId()).orElseThrow(
          () -> new UserNotFoundException(borrowingRequestDTO.userId())
      );

      if (user.getStatus() != UserStatus.ACTIVE)
        throw new UserIsNotActiveException(borrowingRequestDTO.userId());

      if (book.getAvailableQuantity() <= 0)
        throw new BookNotAvailableException(borrowingRequestDTO.bookId());

      Borrowing borrowing = new Borrowing();
      borrowing.setBorrowDate(LocalDateTime.now());
      borrowing.setDueDate(LocalDateTime.now().plusDays(14));
      borrowing.setBook(book);
      borrowing.setUser(user);

      return ResponseEntity.ok(mapper.toBorrowingResponseDTO(borrowingRepository.save(borrowing)));
    } catch (RuntimeException e) {
      return ResponseEntity.status(401).body("Invalid username or password");
    }
  }
}
