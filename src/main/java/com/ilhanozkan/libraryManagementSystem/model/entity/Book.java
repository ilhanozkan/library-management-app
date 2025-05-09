package com.ilhanozkan.libraryManagementSystem.model.entity;

import com.ilhanozkan.libraryManagementSystem.model.enums.BookGenre;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Table(name = "books")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Book {
  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  @Column(nullable = false)
  private String name;

  @Column(nullable = false)
  private String isbn;

  @Column(nullable = false)
  private String author;

  @Column(nullable = false)
  private String publisher;

  @Column(nullable = false)
  @Min(0)
  private Integer numberOfPages;

  @Column(nullable = false)
  @Min(0)
  private Integer quantity;

  @Column(nullable = false)
  @Min(0)
  private Integer availableQuantity;

  @Column(nullable = false)
  private BookGenre bookGenre;

  @PrePersist
  public void onCreate() {
    if (this.id == null)
      this.id = UUID.randomUUID();

    if (quantity == null)
      quantity = 0;

    if (numberOfPages == null)
      numberOfPages = 0;
  }
}

