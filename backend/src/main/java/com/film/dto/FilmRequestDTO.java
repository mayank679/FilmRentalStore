package com.film.dto;

import jakarta.validation.constraints.*;
import lombok.*;
import java.math.BigDecimal;
import java.util.List;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class FilmRequestDTO {

    @NotBlank(message = "Title is required")
    @Size(max = 128, message = "Title must be at most 128 characters")
    private String title;

    private String description;

    private Integer releaseYear;

    @NotNull(message = "Language ID is required")
    private Integer languageId;

    private Integer originalLanguageId;

    @NotNull(message = "Rental duration is required")
    @Min(value = 1, message = "Rental duration must be at least 1")
    private Short rentalDuration;

    @NotNull(message = "Rental rate is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Rental rate must be positive")
    private BigDecimal rentalRate;

    @Min(value = 1, message = "Length must be positive")
    private Integer length;

    @NotNull(message = "Replacement cost is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Replacement cost must be positive")
    private BigDecimal replacementCost;

    @Pattern(regexp = "G|PG|PG-13|R|NC-17",
            message = "Rating must be one of: G, PG, PG-13, R, NC-17")
    private String rating;

    private String specialFeatures;

    // Optional: list of category IDs to assign to this film
    private List<Integer> categoryIds;
}