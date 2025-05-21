package com.infosys.controller;

import com.infosys.beans.Book;
import com.infosys.services.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.Scanner;

@Component
public class BookController {
    private final BookService bookService;
    private final Scanner scanner = new Scanner(System.in);

    @Autowired
    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    public void run() {
        boolean running = true;

        while (running) {
            displayMenu();
            int choice = getChoice();

            switch (choice) {
                case 1:
                    listAllBooks();
                    break;
                case 2:
                    findBookById();
                    break;
                case 3:
                    findBookByIsbn();
                    break;
                case 4:
                    addBook();
                    break;
                case 5:
                    updateBook();
                    break;
                case 6:
                    deleteBook();
                    break;
                case 7:
                    showBookCount();
                    break;
                case 8:
                    running = false;
                    System.out.println("Exiting Library Management System. Goodbye!");
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }

            System.out.println("\nPress Enter to continue...");
            scanner.nextLine();
        }
    }

    private void displayMenu() {
        System.out.println("\n===== LIBRARY MANAGEMENT SYSTEM =====");
        System.out.println("1. List all books");
        System.out.println("2. Find book by ID");
        System.out.println("3. Find book by ISBN");
        System.out.println("4. Add a new book");
        System.out.println("5. Update a book");
        System.out.println("6. Delete a book");
        System.out.println("7. Show total number of books");
        System.out.println("8. Exit");
        System.out.print("Enter your choice: ");
    }

    private int getChoice() {
        try {
            int choice = Integer.parseInt(scanner.nextLine());
            return choice;
        } catch (NumberFormatException e) {
            return -1;  // Invalid choice
        }
    }

    private void listAllBooks() {
        List<Book> books = bookService.getAllBooks();

        if (books.isEmpty()) {
            System.out.println("No books found in the library.");
            return;
        }

        System.out.println("\n===== BOOKS IN LIBRARY =====");
        books.forEach(book -> System.out.println(book));
    }

    private void findBookById() {
        System.out.print("Enter book ID: ");
        String id = scanner.nextLine();

        Optional<Book> book = bookService.getBookById(id);
        if (book.isPresent()) {
            System.out.println("Book found: " + book.get());
        } else {
            System.out.println("Book with ID " + id + " not found.");
        }
    }

    private void findBookByIsbn() {
        System.out.print("Enter book ISBN: ");
        String isbn = scanner.nextLine();

        Optional<Book> book = bookService.getBookByIsbn(isbn);
        if (book.isPresent()) {
            System.out.println("Book found: " + book.get());
        } else {
            System.out.println("Book with ISBN " + isbn + " not found.");
        }
    }

    private void addBook() {
        Book book = new Book();

        System.out.print("Enter book title: ");
        book.setTitle(scanner.nextLine());

        System.out.print("Enter book author: ");
        book.setAuthor(scanner.nextLine());

        System.out.print("Enter book ISBN: ");
        book.setIsbn(scanner.nextLine());

        Book savedBook = bookService.addBook(book);
        System.out.println("Book added successfully: " + savedBook);
    }

    private void updateBook() {
        System.out.print("Enter ID of book to update: ");
        String id = scanner.nextLine();

        Optional<Book> existingBook = bookService.getBookById(id);
        if (existingBook.isEmpty()) {
            System.out.println("Book with ID " + id + " not found.");
            return;
        }

        Book book = existingBook.get();
        System.out.println("Current details: " + book);

        System.out.print("Enter new title (or press Enter to keep current): ");
        String title = scanner.nextLine();
        if (!title.isEmpty()) {
            book.setTitle(title);
        }

        System.out.print("Enter new author (or press Enter to keep current): ");
        String author = scanner.nextLine();
        if (!author.isEmpty()) {
            book.setAuthor(author);
        }

        System.out.print("Enter new ISBN (or press Enter to keep current): ");
        String isbn = scanner.nextLine();
        if (!isbn.isEmpty()) {
            book.setIsbn(isbn);
        }

        Optional<Book> updatedBook = bookService.updateBook(id, book);
        if (updatedBook.isPresent()) {
            System.out.println("Book updated successfully: " + updatedBook.get());
        } else {
            System.out.println("Failed to update book.");
        }
    }

    private void deleteBook() {
        System.out.print("Enter ID of book to delete: ");
        String id = scanner.nextLine();

        boolean deleted = bookService.deleteBook(id);
        if (deleted) {
            System.out.println("Book deleted successfully.");
        } else {
            System.out.println("Book with ID " + id + " not found.");
        }
    }

    private void showBookCount() {
        long count = bookService.getBookCount();
        System.out.println("Total number of books in the library: " + count);
    }
}
