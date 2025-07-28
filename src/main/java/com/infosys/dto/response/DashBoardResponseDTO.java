package com.infosys.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class DashBoardResponseDTO {
    private int Total_Books;
    private int Active_Members;
    private int Books_Borrowed;
    private int Overdue_Books;

}
