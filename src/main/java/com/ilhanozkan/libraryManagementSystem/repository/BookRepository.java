package com.ilhanozkan.libraryManagementSystem.repository;

import com.ilhanozkan.libraryManagementSystem.model.entity.Book;
import com.ilhanozkan.libraryManagementSystem.model.enums.BookGenre;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface BookRepository extends JpaRepository<Book, UUID> {
  @Query("SELECT b FROM Book b WHERE " +
         "(:title IS NULL OR LOWER(b.name) LIKE LOWER(CONCAT('%', :title, '%'))) AND " +
         "(:author IS NULL OR LOWER(b.author) LIKE LOWER(CONCAT('%', :author, '%'))) AND " +
         "(:isbn IS NULL OR LOWER(b.isbn) LIKE LOWER(CONCAT('%', :isbn, '%'))) AND " +
         "(:genre IS NULL OR b.genre = :genre)")
  List<Book> searchBooks(@Param("title") String title, 
                         @Param("author") String author, 
                         @Param("isbn") String isbn, 
                         @Param("genre") BookGenre genre);
  
  Book findByIsbn(String isbn);
}
