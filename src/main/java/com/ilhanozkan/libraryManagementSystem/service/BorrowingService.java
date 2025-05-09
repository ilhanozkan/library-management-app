package com.ilhanozkan.libraryManagementSystem.service;

import com.ilhanozkan.libraryManagementSystem.model.dto.request.BorrowingRequestDTO;
import com.ilhanozkan.libraryManagementSystem.model.dto.response.BorrowingResponseDTO;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface BorrowingService {
  public List<BorrowingResponseDTO> getBorrowings();
  public ResponseEntity<?> createBorrowing(BorrowingRequestDTO borrowingRequestDTO);
}
