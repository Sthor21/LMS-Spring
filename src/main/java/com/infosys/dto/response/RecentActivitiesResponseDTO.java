package com.infosys.dto.response;

import com.infosys.enums.BorrowStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.modelmapper.internal.bytebuddy.asm.Advice;

import java.time.LocalDate;

@NoArgsConstructor
@AllArgsConstructor


@Data
public class RecentActivitiesResponseDTO {
    private Integer borrowID;
    private BorrowStatus borrowStatus;
    private String userName;
    private String bookTitle;
    private LocalDate time;
}
