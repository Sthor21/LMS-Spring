package com.infosys.controller;

import com.infosys.dto.requests.BorrowRequestDto;
import com.infosys.dto.response.BorrowResponseDTO;
import com.infosys.enums.BorrowStatus;
import com.infosys.services.BorrowService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@RestController
@RequestMapping("/borrows")
public class BorrowController {

    @Autowired
    private BorrowService borrowService;

    @PostMapping
    public ResponseEntity<BorrowResponseDTO> borrowBook(@Valid @RequestBody BorrowRequestDto borrowDto) {
        return new ResponseEntity<>(borrowService.borrowBook(borrowDto), HttpStatus.CREATED);
    }

    @PutMapping("/return/{borrowRecordId}")
    public ResponseEntity<BorrowResponseDTO> returnBook(@PathVariable @Positive Integer borrowRecordId) {
        return new ResponseEntity<>(borrowService.returnBook(borrowRecordId), HttpStatus.OK);
    }
    @GetMapping
    public ResponseEntity<List<BorrowResponseDTO>>getAllBorrowRecords(){
        return ResponseEntity.ok(borrowService.getAll());
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<BorrowResponseDTO>> getUserBorrowHistory(@PathVariable @Positive Integer userId) {
        return new ResponseEntity<>(borrowService.getUserBorrowHistory(userId), HttpStatus.OK);
    }

    @GetMapping("/overdue")
    public ResponseEntity<List<BorrowResponseDTO>> getOverdueBooks() {
        return new ResponseEntity<>(borrowService.getOverdueBooks(), HttpStatus.OK);
    }
    @GetMapping("/status/{borrowStatus}")
    public ResponseEntity<List<BorrowResponseDTO>> getBooksByStatus(@PathVariable BorrowStatus borrowStatus){
        return new ResponseEntity<>(borrowService.getBooksByStatus(borrowStatus),HttpStatus.OK);
    }

    @GetMapping("/totalFine")
    public Long getTotalFineAmount() {
        return borrowService.getTotalFineAmount();
    }
}
