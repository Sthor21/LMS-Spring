package com.infosys.dto.response;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class BorrowResponseDTO {
    private int id;
    private int bookId;
    private int userId;
    private String bookTitle;
    private LocalDate borrowDate;
    private LocalDate dueDate;
    private LocalDate returnDate;
    private String status;
    private BigDecimal fineAmount;
}
