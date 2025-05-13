package com.ilhanozkan.libraryManagementSystem.repository;

import com.ilhanozkan.libraryManagementSystem.model.entity.Borrowing;
import com.ilhanozkan.libraryManagementSystem.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

public interface BorrowingRepository extends JpaRepository<Borrowing, UUID> {
  List<Borrowing> findByUserAndReturnedFalse(User user);
  List<Borrowing> findByUser(User user);

  // Find all borrowings that are not returned and are overdue
  @Query(
      value = "SELECT b FROM Borrowing b WHERE b.returned = false AND b.dueDate < CURRENT_DATE"
  )
  List<Borrowing> findOverdueBooks();
}
