package com.infosys.repository;

import com.infosys.beans.Book;
import jakarta.persistence.NamedNativeQuery;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface BookRepository extends JpaRepository<Book, Integer> {
    @Query("SELECT COUNT(b) FROM Book b")
    int countAllBooks();

    List<Book> findByTitleContainingIgnoreCase(String title);
    List<Book> findByAuthorContainingIgnoreCase(String author);
    List<Book> findByGenresContainingIgnoreCase(String genre);
    List<Book> findByIsbn(String isbn);
    List<Book> findByLanguage(String language);
    List<Book> findByPublisher(String publisher);
    List<Book> findByPublishedDateBetween(LocalDate startDate, LocalDate endDate);

    @Query("SELECT b FROM Book b WHERE " +
            "LOWER(b.title) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
            "LOWER(b.author) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
            "LOWER(b.genres) LIKE LOWER(CONCAT('%', :query, '%'))")
    List<Book> searchBooks(@Param("query") String query);
}
