package com.infosys.services;

import com.infosys.beans.Book;
import com.infosys.dto.requests.BookRequestDto;
import com.infosys.dto.response.BookResponseDTO;
import com.infosys.repository.BookRepository;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class BookService {

    @Autowired
    private  BookRepository bookRepository;
    @Autowired
    private ModelMapper modelMapper;

    public List<BookResponseDTO> getAllBooks() {
        List<Book> books = bookRepository.findAll();
        return books.stream()
                .map(book -> modelMapper.map(book, BookResponseDTO.class))
                .collect(Collectors.toList());
    }

    public BookResponseDTO getBookById(int id) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Book not found with id: " + id));
        return modelMapper.map(book, BookResponseDTO.class);
    }

    public List<BookResponseDTO> searchBooks(String keyword) {
        List<Book> books = bookRepository.searchBooks(keyword);
        return books.stream()
                .map(book -> modelMapper.map(book, BookResponseDTO.class))
                .collect(Collectors.toList());
    }

    public BookResponseDTO updateBook(int id, BookRequestDto request) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Book not found with id: " + id));

        book.setTitle(request.getTitle());
        book.setAuthor(request.getAuthor());
        book.setPublisher(request.getPublisher());
        book.setIsbn(request.getIsbn());
        book.setGenres(request.getGenres());
        book.setDescription(request.getDescription());
        book.setLanguage(request.getLanguage());
        book.setTotalCopies(request.getTotalCopies());
        book.setPageCount(request.getPageCount());
        book.setPublishedDate(request.getPublishedDate());

        Book updatedBook = bookRepository.save(book);
        return modelMapper.map(updatedBook, BookResponseDTO.class);
    }

    public void deleteBook(int id) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Book not found with id: " + id));

        if (book.getAvailableCopies() < book.getTotalCopies()) {
            throw new RuntimeException("Cannot delete book with active issues");
        }

        bookRepository.delete(book);
    }

    public BookResponseDTO borrowCopy(int id) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Book not found with id: " + id));

        book.setAvailableCopies(book.getAvailableCopies() -1);
        book.setBorrowedCopies(book.getBorrowedCopies()+1);

        Book updatedBook = bookRepository.save(book);
        return modelMapper.map(updatedBook, BookResponseDTO.class);
    }

    public BookResponseDTO returnCopy(int id) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Book not found with id: " + id));
        book.setAvailableCopies(book.getAvailableCopies() + 1);
        book.setBorrowedCopies(book.getBorrowedCopies()- 1);

        Book updatedBook = bookRepository.save(book);
        return modelMapper.map(updatedBook, BookResponseDTO.class);
    }

    public BookResponseDTO addBook(BookRequestDto request){
        Book book = new Book();
        book.setTitle(request.getTitle());
        book.setAuthor(request.getAuthor());
        book.setPublisher(request.getPublisher());
        book.setIsbn(request.getIsbn());
        book.setGenres(request.getGenres());
        book.setDescription(request.getDescription());
        book.setLanguage(request.getLanguage());
        book.setTotalCopies(request.getTotalCopies());
        book.setPageCount(request.getPageCount());
        book.setPublishedDate(request.getPublishedDate());
        book.setAvailableCopies(request.getTotalCopies());
        bookRepository.save(book);
        return modelMapper.map(book,BookResponseDTO.class);
    }

    public BookResponseDTO updateBookCopies(int id, int additionalCopies) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Book not found with id: " + id));

        book.setTotalCopies(book.getTotalCopies() + additionalCopies);
        book.setAvailableCopies(book.getAvailableCopies() + additionalCopies);

        Book updatedBook = bookRepository.save(book);
        return modelMapper.map(updatedBook, BookResponseDTO.class);
    }
}
