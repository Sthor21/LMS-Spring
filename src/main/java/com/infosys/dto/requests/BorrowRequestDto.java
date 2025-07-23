package com.infosys.dto.requests;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BorrowRequestDto {
    @NotNull(message = "Book ID is required")
    @Positive(message = "Book ID must be positive")
    private int bookId;
    @Positive(message = "User ID must be positive")
    private int userId;

    @FutureOrPresent(message = "Due date must be today or in the future")
    private LocalDate dueDate;
}