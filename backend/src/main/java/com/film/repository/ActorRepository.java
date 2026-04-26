package com.film.repository;

import com.film.dto.ActorResponseDTO;
import com.film.entity.Actor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
 
@Repository
public interface ActorRepository extends JpaRepository<Actor, Integer> {
	// Partial match on first name — case-insensitive
    List<Actor> findByFirstNameContainingIgnoreCase(String firstName);
 
    // Partial match on last name — case-insensitive
    List<Actor> findByLastNameContainingIgnoreCase(String lastName);
 
    // Exact match on both first name and last name — case-insensitive
    Optional<Actor> findByFirstNameIgnoreCaseAndLastNameIgnoreCase(
            String firstName, String lastName);
 
    // last_update BETWEEN from AND to
    List<Actor> findByLastUpdateBetween(LocalDateTime from, LocalDateTime to);
 
    // last_update > from
    List<Actor> findByLastUpdateAfter(LocalDateTime from);
 
    // last_update < to
    List<Actor> findByLastUpdateBefore(LocalDateTime to);

	Actor save(ActorResponseDTO actor);
}
 