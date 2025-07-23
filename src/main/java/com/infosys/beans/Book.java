package com.infosys.beans;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name = "books")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Book {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String author;

    @Column(columnDefinition = "TEXT")
    private String description;

    private String publisher;

    @Column(name = "page_count")
    private Integer pageCount;

    @Column(columnDefinition = "TEXT")
    private String genres;

    @Column(length = 50, unique = true)
    private String isbn;

    @Column(length = 50)
    private String language;

    @Column(name = "published_date")
    private LocalDate publishedDate;

    // Inventory fields
    @Column(name = "total_copies", nullable = false, columnDefinition = "integer default 0")
    private Integer totalCopies = 0;

    @Column(name = "available_copies", nullable = false, columnDefinition = "integer default 0")
    private Integer availableCopies = 0;

    @Column(name = "borrowed_copies", nullable = false, columnDefinition = "integer default 0")
    private Integer borrowedCopies = 0;
}