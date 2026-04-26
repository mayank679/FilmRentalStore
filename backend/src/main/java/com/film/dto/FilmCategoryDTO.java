package com.film.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
 
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FilmCategoryDTO {
 
    @NotNull(message = "filmId is required")
    private Integer filmId;
 
    @NotNull(message = "categoryId is required")
    private Integer categoryId;
}