package com.infosys.controller;

import com.infosys.dto.requests.BookRequestDto;
import com.infosys.dto.response.BookResponseDTO;
import com.infosys.services.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/books")
public class BookController {

    @Autowired
    private BookService bookService;

    @GetMapping
    public ResponseEntity<List<BookResponseDTO>> getAllBooks() {
        return new ResponseEntity<>(bookService.getAllBooks(), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<BookResponseDTO> getBookById(@PathVariable int id) {
        return new ResponseEntity<>(bookService.getBookById(id), HttpStatus.OK);
    }

    @GetMapping("/search")
    public ResponseEntity<List<BookResponseDTO>> searchBooks(@RequestParam String keyword) {
        return new ResponseEntity<>(bookService.searchBooks(keyword), HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<BookResponseDTO> updateBook(@PathVariable int id, @RequestBody BookRequestDto request) {
        return new ResponseEntity<>(bookService.updateBook(id, request), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteBook(@PathVariable int id) {
        bookService.deleteBook(id);
        return ResponseEntity.ok("Deleted Successfully");
    }

    @PatchMapping("/{id}/copies")
    public ResponseEntity<BookResponseDTO> updateBookCopies(@PathVariable int id, @RequestParam int additionalCopies) {
        return new ResponseEntity<>(bookService.updateBookCopies(id, additionalCopies), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<BookResponseDTO> addBook(@RequestBody BookRequestDto request) {
        return new ResponseEntity<>(bookService.addBook(request), HttpStatus.OK);
    }
}