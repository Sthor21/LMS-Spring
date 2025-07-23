package com.infosys.dto.response;

import lombok.Data;

import java.time.LocalDate;

@Data
public class BookResponseDTO {
    private Integer id;
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
    private Integer availableCopies;
    private Integer borrowedCopies;
}