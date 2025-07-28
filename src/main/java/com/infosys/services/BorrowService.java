package com.infosys.services;

import com.infosys.beans.Book;
import com.infosys.beans.BorrowRecord;
import com.infosys.beans.User;
import com.infosys.dto.requests.BorrowRequestDto;
import com.infosys.dto.response.BorrowResponseDTO;
import com.infosys.enums.BorrowStatus;
import com.infosys.repository.BookRepository;
import com.infosys.repository.BorrowRecordRepository;
import com.infosys.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class BorrowService {

    @Autowired
    private BorrowRecordRepository borrowRecordRepository;
    @Autowired
    private BookRepository bookRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private BookService bookService;
    private final BigDecimal FINE_PER_DAY = new BigDecimal("1.00");

    public List<BorrowResponseDTO>getAll(){
        return borrowRecordRepository.findAll()
                .stream()
                .map(borrowRecord -> modelMapper.map(borrowRecord,BorrowResponseDTO.class))
                .collect((Collectors.toList()));
    }

    public BorrowResponseDTO borrowBook(BorrowRequestDto borrowDto) {
        User user = userRepository.findById(borrowDto.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Validate book exists and is available
        Book book = bookRepository.findById(borrowDto.getBookId())
                .orElseThrow(() -> new RuntimeException("Book not found"));

        if (book.getAvailableCopies() <= 0) {
            throw new RuntimeException("No available copies of this book");
        }

        // Check if user already has this book borrowed
        if (borrowRecordRepository.existsByUserIdAndBookIdAndStatus(
                borrowDto.getUserId(), borrowDto.getBookId(), BorrowStatus.ACTIVE)) {
            throw new RuntimeException("User already has this book borrowed");
        }

        // Create borrow record
        BorrowRecord borrowRecord = new BorrowRecord();
        borrowRecord.setUser(user);
        borrowRecord.setBook(book);
        borrowRecord.setBorrowDate(LocalDate.now());
        borrowRecord.setDueDate(borrowDto.getDueDate());
        borrowRecord.setStatus(BorrowStatus.ACTIVE);
        borrowRecord.setFineAmount(BigDecimal.ZERO);

        // Update book inventory
        bookService.borrowCopy(book.getId());

        BorrowRecord savedRecord = borrowRecordRepository.save(borrowRecord);
        return modelMapper.map(savedRecord, BorrowResponseDTO.class);
    }

    public BorrowResponseDTO returnBook(Integer borrowRecordId) {
        BorrowRecord borrowRecord = borrowRecordRepository.findById(borrowRecordId)
                .orElseThrow(() -> new RuntimeException("Borrow record not found"));

        if (borrowRecord.getStatus() != BorrowStatus.ACTIVE) {
            throw new RuntimeException("Book is not currently borrowed");
        }

        // Calculate fine if overdue
        LocalDate today = LocalDate.now();
        if (today.isAfter(borrowRecord.getDueDate())) {
            long daysOverdue = ChronoUnit.DAYS.between(borrowRecord.getDueDate(), today);
            BigDecimal fine = FINE_PER_DAY.multiply(BigDecimal.valueOf(daysOverdue));
            borrowRecord.setFineAmount(fine);
        }

        // Update record
        borrowRecord.setReturnDate(today);
        borrowRecord.setStatus(BorrowStatus.RETURNED);

        // Update book inventory
        Book book = borrowRecord.getBook();
        bookService.returnCopy(book.getId());

        BorrowRecord updatedRecord = borrowRecordRepository.save(borrowRecord);
        return modelMapper.map(updatedRecord, BorrowResponseDTO.class);
    }

    public List<BorrowResponseDTO> getUserBorrowHistory(Integer userId) {
        return borrowRecordRepository.findByUser_Id(userId).stream()
                .map(borrowRecord -> modelMapper.map(borrowRecord, BorrowResponseDTO.class))
                .collect(Collectors.toList());
    }

    public List<BorrowResponseDTO> getOverdueBooks() {
        return borrowRecordRepository.findByDueDateBeforeAndStatus(LocalDate.now(), BorrowStatus.ACTIVE)
                .stream()
                .map(borrowRecord -> modelMapper.map(borrowRecord, BorrowResponseDTO.class))
                .collect(Collectors.toList());
    }

    public List<BorrowResponseDTO> getBooksByStatus(BorrowStatus borrowStatus){
        return borrowRecordRepository.findByStatus(borrowStatus).
                stream()
                .map(borrowRecord -> modelMapper.map(borrowRecord,BorrowResponseDTO.class))
                .collect(Collectors.toList());
    }

    public Long getTotalFineAmount() {
        long totalFine = borrowRecordRepository.getTotalFineAmount();
        return totalFine;
    }

    public List<BorrowResponseDTO> searchBorrowRecords(String keyword) {
        List<BorrowRecord> records = borrowRecordRepository.searchBorrowRecords(keyword);
        return records.stream().map(borrowRecord -> modelMapper.
                map(borrowRecord, BorrowResponseDTO.class)).
                collect(Collectors.toList());
    }




}
