package com.ilhanozkan.libraryManagementSystem.service;

import com.ilhanozkan.libraryManagementSystem.repository.BorrowingRepository;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

public class BorrowingServiceTest {

  @Mock
  private BorrowingRepository borrowingRepository;

  @InjectMocks
  private BorrowingService borrowingService;

  @Test
  void borrowBookSuccess() {

  }

  @Test
  void borrowBookFailure() {

  }

  @Test
  void returnBookSuccess() {

  }

  @Test
  void returnBookFailure() {

  }
}
