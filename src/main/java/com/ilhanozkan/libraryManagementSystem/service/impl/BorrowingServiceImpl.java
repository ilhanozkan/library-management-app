package com.ilhanozkan.libraryManagementSystem.service.impl;

import com.ilhanozkan.libraryManagementSystem.common.exception.book.BookAlreadyReturnedException;
import com.ilhanozkan.libraryManagementSystem.common.exception.book.BookNotAvailableException;
import com.ilhanozkan.libraryManagementSystem.common.exception.book.BookNotFoundException;
import com.ilhanozkan.libraryManagementSystem.common.exception.borrowing.BorrowingNotFoundException;
import com.ilhanozkan.libraryManagementSystem.common.exception.user.UserIsNotActiveException;
import com.ilhanozkan.libraryManagementSystem.common.exception.user.UserNotFoundException;
import com.ilhanozkan.libraryManagementSystem.model.dto.request.BorrowingRequestDTO;
import com.ilhanozkan.libraryManagementSystem.model.dto.response.BorrowingResponseDTO;
import com.ilhanozkan.libraryManagementSystem.model.entity.Book;
import com.ilhanozkan.libraryManagementSystem.model.entity.Borrowing;
import com.ilhanozkan.libraryManagementSystem.model.entity.User;
import com.ilhanozkan.libraryManagementSystem.model.enums.UserStatus;
import com.ilhanozkan.libraryManagementSystem.model.mapper.BorrowingResponseDTOMapper;
import com.ilhanozkan.libraryManagementSystem.repository.BookRepository;
import com.ilhanozkan.libraryManagementSystem.repository.BorrowingRepository;
import com.ilhanozkan.libraryManagementSystem.repository.UserRepository;
import com.ilhanozkan.libraryManagementSystem.service.BookService;
import com.ilhanozkan.libraryManagementSystem.service.BorrowingService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BorrowingServiceImpl implements BorrowingService {
  private final BorrowingRepository borrowingRepository;
  private final BookRepository bookRepository;
  private final UserRepository userRepository;
  private final BookService bookService;
  private final BorrowingResponseDTOMapper mapper = BorrowingResponseDTOMapper.INSTANCE;

  @Transactional
  public List<BorrowingResponseDTO> getBorrowings() {
    try {
      return mapper.toBorrowingResponseDTOList(borrowingRepository.findAll());
    } catch (RuntimeException e) {
      throw new RuntimeException(e.getMessage());
    }
  }

  @Transactional
  public List<BorrowingResponseDTO> getBorrowingsByUserId(UUID userId) {
    User user = userRepository.findById(userId)
        .orElseThrow(() -> new UserNotFoundException(userId));

    return mapper.toBorrowingResponseDTOList(borrowingRepository.findByUser(user));
  }

  @Transactional
  public List<BorrowingResponseDTO> getActiveBorrowingsByUserId(UUID userId) {
    User user = userRepository.findById(userId)
        .orElseThrow(() -> new UserNotFoundException(userId));

    return mapper.toBorrowingResponseDTOList(borrowingRepository.findByUserAndReturnedFalse(user));
  }

  @Transactional
  public BorrowingResponseDTO createBorrowing(BorrowingRequestDTO borrowingRequestDTO) {
      Book book = bookRepository.findById(borrowingRequestDTO.getBookId()).orElseThrow(
          () -> new BookNotFoundException(borrowingRequestDTO.getBookId())
      );

      User user = userRepository.findById(borrowingRequestDTO.getUserId()).orElseThrow(
          () -> new UserNotFoundException(borrowingRequestDTO.getUserId())
      );

      if (user.getStatus() != UserStatus.ACTIVE)
        throw new UserIsNotActiveException(borrowingRequestDTO.getUserId());

      if (book.getAvailableQuantity() <= 0)
        throw new BookNotAvailableException(borrowingRequestDTO.getBookId());

      Borrowing borrowing = new Borrowing();
      borrowing.setBorrowDate(LocalDateTime.now());
      borrowing.setDueDate(LocalDateTime.now().plusDays(14));
      borrowing.setBook(book);
      borrowing.setUser(user);

      return mapper.toBorrowingResponseDTO(borrowingRepository.save(borrowing));
  }

  @Transactional
  public BorrowingResponseDTO returnBook(UUID id) {
      Borrowing borrowing = borrowingRepository.findById(id).orElseThrow(
          () -> new BorrowingNotFoundException(id)
      );

      if (borrowing.getReturned())
        throw new BookAlreadyReturnedException(id);

      borrowing.setReturned(true);
      borrowing.setReturnDate(LocalDateTime.now());
      bookService.updateBookQuantity(borrowing.getBook().getId(), 1);

      return mapper.toBorrowingResponseDTO(borrowingRepository.save(borrowing));
  }

  @Transactional
  public void deleteBorrowing(UUID id) {
    Borrowing borrowing = borrowingRepository.findById(id)
        .orElseThrow(() -> new BorrowingNotFoundException(id));

    if (!borrowing.getReturned())
      bookService.updateBookQuantity(borrowing.getBook().getId(), 1);


    borrowingRepository.delete(borrowing);
  }
}
