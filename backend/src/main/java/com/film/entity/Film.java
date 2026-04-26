package com.film.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import com.fasterxml.jackson.annotation.JsonBackReference;

@Getter
@Setter
@Entity
@Table(name = "film")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Film {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "film_id", columnDefinition = "SMALLINT UNSIGNED")
	private Integer filmId;

	@Column(name = "title", nullable = false)
	private String title;

	@Column(name = "description", columnDefinition = "TEXT")
	private String description;

	@Column(name = "release_year")
	private Integer releaseYear;

	// FK: film.original_language_id → language.language_id
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "original_language_id", foreignKey = @ForeignKey(name = "fk_film_language_original"))
	private Language originalLanguage;

    // FK: film.language_id → language.language_id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "language_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_film_language"))
    @JsonBackReference 
    private Language language;


	@Column(name = "rental_duration", nullable = false)
	private Short rentalDuration;

	@Column(name = "rental_rate", nullable = false)
	private BigDecimal rentalRate;

	@Column(name = "length")
	private Integer length;

	@Column(name = "replacement_cost")
	private BigDecimal replacementCost;


	@Column(name = "rating")
	private String rating;

	@Column(name = "special_features")
	private String specialFeatures;

	@Column(name = "last_update")
	private LocalDateTime lastUpdate;

	// Relations
	@OneToMany(mappedBy = "film", fetch = FetchType.LAZY)
	private List<FilmActor> filmActors;

	@OneToMany(mappedBy = "film", fetch = FetchType.LAZY)
	private List<FilmCategory> filmCategories;

	
}