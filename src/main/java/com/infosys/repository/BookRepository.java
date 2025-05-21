package com.infosys.repository;

import com.infosys.beans.Book;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class BookRepository {
    private final List<Book> books = new ArrayList<>();

    // Add initial sample books
    public BookRepository() {
        books.add(new Book(UUID.randomUUID().toString(), "To Kill a Mockingbird", "Harper Lee", "9780061120084"));
        books.add(new Book(UUID.randomUUID().toString(), "1984", "George Orwell", "9780451524935"));
        books.add(new Book(UUID.randomUUID().toString(), "The Great Gatsby", "F. Scott Fitzgerald", "9780743273565"));
    }

    public List<Book> findAll() {
        return new ArrayList<>(books);
    }

    public Optional<Book> findById(String id) {
        return books.stream()
                .filter(book -> book.getId().equals(id))
                .findFirst();
    }

    public Optional<Book> findByIsbn(String isbn) {
        return books.stream()
                .filter(book -> book.getIsbn().equals(isbn))
                .findFirst();
    }

    public Book save(Book book) {
        if (book.getId() == null || book.getId().isEmpty()) {
            book.setId(UUID.randomUUID().toString());
        } else {
            // Remove existing book if it's an update operation
            books.removeIf(b -> b.getId().equals(book.getId()));
        }
        books.add(book);
        return book;
    }

    public boolean deleteById(String id) {
        return books.removeIf(book -> book.getId().equals(id));
    }

    public long count() {
        return books.size();
    }
}
