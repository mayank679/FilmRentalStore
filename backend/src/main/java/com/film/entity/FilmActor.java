package com.film.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnore;

// Surrogate PK used instead of composite key.
// Unique constraint enforces the original PK (actor_id, film_id).
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "film_actor")
public class FilmActor {

    @EmbeddedId
    private FilmActorId id;  // composite key, NOT a single `id` column

    @ManyToOne
    @MapsId("filmId")
    @JoinColumn(name = "film_id", columnDefinition = "SMALLINT UNSIGNED")
    @JsonIgnore
    private Film film;

    @ManyToOne
    @MapsId("actorId")
    @JoinColumn(name = "actor_id", columnDefinition = "SMALLINT UNSIGNED")
    @JsonIgnore
    private Actor actor;

    @Column(name = "last_update")
    private LocalDateTime lastUpdate;

   
}