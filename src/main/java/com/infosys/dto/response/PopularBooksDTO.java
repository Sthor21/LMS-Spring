package com.infosys.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class PopularBooksDTO {
    private String title;
    private long borrows;
    private double rating;
}
