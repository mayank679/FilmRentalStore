package com.film.dto;

import java.time.LocalDateTime;

import com.film.entity.FilmActorId;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

//── FilmActorResponseDTO.java — RESPONSE (what server returns) ─────
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FilmActorResponseDTO {

 private FilmActorId id;
 private Integer filmId;
 private String filmTitle;
 private Integer actorId;
 private String actorFirstName;
 private String actorLastName;
 private LocalDateTime lastUpdate;
}
