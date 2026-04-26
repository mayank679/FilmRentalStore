package com.film.dto;
 
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;
 
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FilmTextDetailResponseDTO {
 
    // ── film_text fields ─────────────────────────────────────────
    private Integer filmId;
    private String  title;
    private String  description;
 
    // ── film fields (resolved via FK) ────────────────────────────
    private Integer    releaseYear;
    private String     language;
    private String     originalLanguage;
    private Short    rentalDuration;
    private BigDecimal rentalRate;
    private Integer    length;
    private BigDecimal replacementCost;
    private String     rating;
    private String     specialFeatures;
    private LocalDateTime lastUpdate;
}