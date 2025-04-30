package com.ilhanozkan.libraryManagementSystem.repository;

import com.ilhanozkan.libraryManagementSystem.model.entity.Borrowing;
import com.ilhanozkan.libraryManagementSystem.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface BorrowingRepository extends JpaRepository<Borrowing, UUID> {
}
