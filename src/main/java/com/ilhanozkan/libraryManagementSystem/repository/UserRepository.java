package com.ilhanozkan.libraryManagementSystem.repository;

import com.ilhanozkan.libraryManagementSystem.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {
  boolean existsByUsername(String username);
  boolean existsByEmail(String email);

  @Override
  void deleteById(UUID uuid);

  User findByUsername(String username);

  Optional<User> findByEmail(String email);
}
