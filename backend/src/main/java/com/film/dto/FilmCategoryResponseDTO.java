package com.film.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
 
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FilmCategoryResponseDTO {
 
    // Composite key fields (flat — no nesting)
    private Integer filmId;
    private Integer categoryId;
 
    // Resolved names from joined entities
    private String filmTitle;
    private String categoryName;
 
    // Extra film details
    private String  filmRating;
    private Integer releaseYear;
    private LocalDateTime lastUpdate;
}