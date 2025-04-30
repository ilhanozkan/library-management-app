package com.ilhanozkan.libraryManagementSystem.controller;

import com.ilhanozkan.libraryManagementSystem.model.dto.response.BorrowingResponseDTO;
import com.ilhanozkan.libraryManagementSystem.service.BorrowingService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Description;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "Borrowing Operations")
@RestController
@RequestMapping("/borrowings")
public class BorrowingController {
  private final BorrowingService borrowingService;

  @Autowired
  public BorrowingController(BorrowingService borrowingService) {
    this.borrowingService = borrowingService;
  }

  @Description("List user's all borrowings with filters")
  @GetMapping
  public List<BorrowingResponseDTO> getBorrowings() {
    try {
      return borrowingService.getBorrowings();
    } catch (RuntimeException e) {
      throw new RuntimeException(e.getMessage());
    }
  }
}
