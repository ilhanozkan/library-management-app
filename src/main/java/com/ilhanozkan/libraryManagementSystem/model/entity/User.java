package com.ilhanozkan.libraryManagementSystem.model.entity;

import com.ilhanozkan.libraryManagementSystem.model.enums.UserRole;
import com.ilhanozkan.libraryManagementSystem.model.enums.UserStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Table(name = "users")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class User {
  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  @Column(nullable = false, unique = true)
  private String username;

  @Column(nullable = false, unique = true)
  @Email
  private String email;

  @Column(nullable = false)
  private String password;

  @Column(nullable = false)
  private String name;

  @Column(nullable = false)
  private String surname;

  @Column(nullable = false)
  private UserRole role;

  @Column(nullable = false)
  private UserStatus status;

  @PrePersist
  public void onCreate() {
    if (this.id == null)
      this.id = UUID.randomUUID();

    if  (this.status == null)
      this.status = UserStatus.ACTIVE;
  }
}
