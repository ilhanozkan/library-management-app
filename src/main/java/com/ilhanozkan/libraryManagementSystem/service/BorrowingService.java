package com.ilhanozkan.libraryManagementSystem.service;

import com.ilhanozkan.libraryManagementSystem.model.dto.response.BorrowingResponseDTO;

import java.util.List;

public interface BorrowingService {
  public List<BorrowingResponseDTO> getBorrowings();
}
