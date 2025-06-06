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
  // Validation for ISBN-13 format
  @jakarta.validation.constraints.Pattern(
      regexp = "^(978|979)\\d{10}$",
      message = "ISBN must be in the format 978XXXXXXXXXX or 979XXXXXXXXXX")
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
  private BookGenre genre;

  @PrePersist
  public void onCreate() {
    if (this.id == null)
      this.id = UUID.randomUUID();

    if (quantity == null)
      quantity = 0;

    if (numberOfPages == null)
      numberOfPages = 0;
      
    if (availableQuantity == null)
      availableQuantity = quantity;
  }

  @PreUpdate
  public void onUpdate() {
    if (availableQuantity == null)
      availableQuantity = quantity;
    if (availableQuantity > quantity)
      availableQuantity = quantity;
    if (availableQuantity < 0)
      availableQuantity = 0;
    if (quantity < 0)
      quantity = 0;
    if (numberOfPages < 0)
      numberOfPages = 0;
    if (name == null)
      name = "";
    if (isbn == null)
      isbn = "";
    if (author == null)
      author = "";
    if (publisher == null)
      publisher = "";
    if (genre == null)
      genre = BookGenre.OTHER;
    if (isbn.length() != 13)
      throw new IllegalArgumentException("ISBN must be 13 characters long");
  }
}

