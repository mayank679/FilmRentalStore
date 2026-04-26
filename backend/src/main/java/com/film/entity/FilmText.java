package com.film.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "film_text")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FilmText {

    @Id
    private Integer filmId;

    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;
    
    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "film_id", insertable = false, updatable = false)
    private Film film;
}