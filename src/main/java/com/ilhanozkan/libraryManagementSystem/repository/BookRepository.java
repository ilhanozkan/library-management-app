package com.ilhanozkan.libraryManagementSystem.repository;

import com.ilhanozkan.libraryManagementSystem.model.entity.Book;
import com.ilhanozkan.libraryManagementSystem.model.enums.BookGenre;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface BookRepository extends JpaRepository<Book, UUID> {
  @Query(value = "SELECT * FROM books b WHERE " +
         "(:title IS NULL OR b.name ILIKE CONCAT('%', :title, '%')) AND " +
         "(:author IS NULL OR b.author ILIKE CONCAT('%', :author, '%')) AND " +
         "(:isbn IS NULL OR b.isbn ILIKE CONCAT('%', :isbn, '%')) AND " +
         "(:genre IS NULL OR b.genre::text = :genre)",
         nativeQuery = true)
  List<Book> searchBooks(@Param("title") String title, 
                         @Param("author") String author, 
                         @Param("isbn") String isbn, 
                         @Param("genre") String genre);
  
  Book findByIsbn(String isbn);
}
