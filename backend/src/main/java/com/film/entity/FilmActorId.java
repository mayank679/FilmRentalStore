package com.film.entity;

import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Embeddable
@Data
@NoArgsConstructor 
@AllArgsConstructor
@EqualsAndHashCode
public  class FilmActorId implements Serializable {

    @Column(name = "actor_id", columnDefinition = "SMALLINT UNSIGNED")
    private Integer actorId;

    @Column(name = "film_id", columnDefinition = "SMALLINT UNSIGNED")
    private Integer filmId;
}

