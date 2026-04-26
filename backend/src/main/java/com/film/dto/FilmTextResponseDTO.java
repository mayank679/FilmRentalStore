package com.film.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
 
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FilmTextResponseDTO {
 
    private Integer filmId;
    private String  title;
    private String  description;
 
    // Extra film details resolved from the Film relationship
    private String  filmRating;
    private Integer releaseYear;
    private String  language;
}
