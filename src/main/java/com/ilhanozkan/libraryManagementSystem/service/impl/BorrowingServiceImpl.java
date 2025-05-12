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
import com.ilhanozkan.libraryManagementSystem.util.CustomDateTimeFormatter;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayOutputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
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


  @Transactional
  public byte[] getOverdueBooksPDFReport() {
    ByteArrayOutputStream baos = new ByteArrayOutputStream();

    PdfWriter writer = new PdfWriter(baos);
    PdfDocument pdf = new PdfDocument(writer);
    Document document = new Document(pdf);

    document.add(new Paragraph(getOverdueBooksTextReport()));
    document.close();

    byte[] pdfBytes = baos.toByteArray();

    return pdfBytes;
  }

  @Transactional
  public String getOverdueBooksTextReport() {

    StringBuilder report = new StringBuilder();
    report.append("Overdue Books Report\n");
    report.append("Report Generation Date: ").append(CustomDateTimeFormatter.formatDateTime(java.time.LocalDateTime.now())).append("\n\n");

    List<Borrowing> overdueBooks = borrowingRepository.findOverdueBooks();

    report.append("Total Borrowings Count: ").append(borrowingRepository.count()).append("\n");
    report.append("Total Overdue Books Count: ").append(overdueBooks.size()).append("\n\n");

    report.append("Overdue Books:\n");
    report.append("-------------------------\n");
    for (Borrowing borrowing : overdueBooks) {
      report.append("Book ID: ").append(borrowing.getBook().getId()).append("\n");
      report.append("Book Name: ").append(borrowing.getBook().getName()).append("\n");
      report.append("Book Author: ").append(borrowing.getBook().getAuthor()).append("\n");
      report.append("Borrow Date: ").append(CustomDateTimeFormatter.formatDateTime(borrowing.getBorrowDate())).append("\n");

      // Using CustomDateTimeFormatter to format the due date
      report.append("Due Date: ").append(CustomDateTimeFormatter.formatDateTime(borrowing.getDueDate())).append("\n");
      report.append("User ID: ").append(borrowing.getUser().getId()).append("\n");
      report.append("User Name: ").append(borrowing.getUser().getName()).append("\n");
      report.append("User Surname: ").append(borrowing.getUser().getSurname()).append("\n");
      report.append("User Email: ").append(borrowing.getUser().getEmail()).append("\n");
      report.append("-------------------------\n");
    }

    return report.toString();
  }
}
