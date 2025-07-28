package com.infosys.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class BookBorrowStats {
    private Long bookId;
    private Long borrowCount;
}
