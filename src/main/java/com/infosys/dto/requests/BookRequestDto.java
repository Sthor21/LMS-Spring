package com.infosys.dto.requests;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookRequestDto {
    private String title;
    private String author;
    private String description;
    private String publisher;
    private Integer pageCount;
    private String genres;
    private String isbn;
    private String language;
    private LocalDate publishedDate;
    private Integer totalCopies;
}
