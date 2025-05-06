package com.ilhanozkan.libraryManagementSystem.service.impl;

import com.ilhanozkan.libraryManagementSystem.model.dto.response.BorrowingResponseDTO;
import com.ilhanozkan.libraryManagementSystem.model.mapper.BorrowingResponseDTOMapper;
import com.ilhanozkan.libraryManagementSystem.repository.BorrowingRepository;
import com.ilhanozkan.libraryManagementSystem.service.BorrowingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BorrowingServiceImpl implements BorrowingService {
  private final BorrowingRepository borrowingRepository;
  private final BorrowingResponseDTOMapper mapper = BorrowingResponseDTOMapper.INSTANCE;

  @Autowired
  public BorrowingServiceImpl(BorrowingRepository borrowingRepository) {
    this.borrowingRepository = borrowingRepository;
  }

  public List<BorrowingResponseDTO> getBorrowings() {
    try {
      return mapper.toBorrowingResponseDTOList(borrowingRepository.findAll());
    } catch (RuntimeException e) {
      throw new RuntimeException(e.getMessage());
    }
  }
}
