package com.film.repository;

import com.film.entity.FilmActor;
import com.film.entity.FilmActorId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface FilmActorRepository extends JpaRepository<FilmActor, FilmActorId> {

    // All actor assignments for a given film
    List<FilmActor> findByFilm_FilmId(Integer filmId);

    // All film assignments for a given actor
    List<FilmActor> findByActor_ActorId(Integer actorId);

    // Check duplicate before insert
    boolean existsById(FilmActorId id);
}