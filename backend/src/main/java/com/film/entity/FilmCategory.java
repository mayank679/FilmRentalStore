package com.film.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "film_category")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class FilmCategory {

    @EmbeddedId
    private FilmCategoryId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("filmId")
    @JoinColumn(name = "film_id", nullable = false,
            columnDefinition = "SMALLINT UNSIGNED",
            foreignKey = @ForeignKey(name = "fk_film_category_film"))
    private Film film;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("categoryId")
    @JoinColumn(name = "category_id", nullable = false,
            columnDefinition = "TINYINT UNSIGNED",
            foreignKey = @ForeignKey(name = "fk_film_category_category"))
    private Category category;

    @Column(name = "last_update", insertable = false, updatable = false)
    private LocalDateTime lastUpdate;
}