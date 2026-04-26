package com.film.dto;

import java.time.LocalDateTime;

import com.film.entity.Actor;
import com.film.entity.Film;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

//── FilmActorDTO.java — REQUEST (what client sends) ────────────────
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FilmActorDTO {

 @NotNull(message = "filmId is required")
 private Integer filmId;

 @NotNull(message = "actorId is required")
 private Integer actorId;
}
