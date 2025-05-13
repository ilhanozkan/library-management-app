package com.ilhanozkan.libraryManagementSystem.service;

import com.ilhanozkan.libraryManagementSystem.model.dto.request.BorrowingRequestDTO;
import com.ilhanozkan.libraryManagementSystem.model.dto.response.BorrowingResponseDTO;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.UUID;

public interface BorrowingService {
  public List<BorrowingResponseDTO> getBorrowings();
  public List<BorrowingResponseDTO> getBorrowingsByUserId(UUID userId);
  public List<BorrowingResponseDTO> getActiveBorrowingsByUserId(UUID userId);
  public BorrowingResponseDTO createBorrowing(BorrowingRequestDTO borrowingRequestDTO);
  public BorrowingResponseDTO returnBook(UUID id);
  public void deleteBorrowing(UUID id);
}
