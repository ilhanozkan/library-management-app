package com.ilhanozkan.libraryManagementSystem.service;

import com.ilhanozkan.libraryManagementSystem.model.dto.response.BorrowingResponseDTO;
import com.ilhanozkan.libraryManagementSystem.model.entity.Borrowing;
import com.ilhanozkan.libraryManagementSystem.model.mapper.BorrowingResponseDTOMapper;
import com.ilhanozkan.libraryManagementSystem.repository.BorrowingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BorrowingService {
  private final BorrowingRepository borrowingRepository;
  private final BorrowingResponseDTOMapper mapper = BorrowingResponseDTOMapper.INSTANCE;

  @Autowired
  public BorrowingService(BorrowingRepository borrowingRepository) {
    this.borrowingRepository = borrowingRepository;
  }

  public List<BorrowingResponseDTO> getBorrowings() {
    try {
      List<Borrowing> borrowings = borrowingRepository.findAll();

      return mapper.toBorrowingResponseDTOList(borrowings);
    } catch (RuntimeException e) {
      throw new RuntimeException(e.getMessage());
    }
  }
}
