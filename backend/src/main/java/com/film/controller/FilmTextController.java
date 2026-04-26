package com.film.controller;



import com.film.dto.FilmTextDTO;
import com.film.dto.FilmTextDetailResponseDTO;
import com.film.dto.FilmTextResponseDTO;
import com.film.services.FilmTextService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import org.springframework.data.domain.Page;
import java.util.List;

@RestController
@RequestMapping("/api/filmtexts")
@RequiredArgsConstructor
public class FilmTextController {

    private final FilmTextService filmTextService;

    // GET all film texts (paginated)
    @GetMapping("/paged")
    public ResponseEntity<Page<FilmTextResponseDTO>> getAllFilmTextsPaged(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(filmTextService.findAllPaginated(page, size));
    }

    @GetMapping
    public ResponseEntity<List<FilmTextResponseDTO>> getAllFilmTexts() {
        return ResponseEntity.ok(filmTextService.getAll());
    }
 
 
    // ────────────────────────────────────────────────────────────────
    // GET /api/v1/film-texts/{filmId}
    //
    // Returns film_text record + ALL Film details joined via FK
    // film_text.film_id → film.film_id
    //
    // Response includes:
    //   filmId, title, description         ← from film_text
    //   releaseYear, language, rating,     ← from film (joined)
    //   rentalRate, rentalDuration,
    //   replacementCost, length,
    //   specialFeatures, lastUpdate
    //
    // Example:
    //   GET http://localhost:8082/api/v1/film-texts/1
    // ────────────────────────────────────────────────────────────────
    @GetMapping("/{filmId}")
    public ResponseEntity<FilmTextDetailResponseDTO> getByFilmIdWithDetails(
            @PathVariable Integer filmId) {
        return ResponseEntity.ok(
                filmTextService.getByFilmIdWithDetails(filmId));
    }
 
 
    // ────────────────────────────────────────────────────────────────
    // GET /api/v1/film-texts/search/title?title=academy
    //
    // Returns all film_text records whose title contains
    // the given keyword (case-insensitive partial match)
    //
    // Example:
    //   GET http://localhost:8082/api/v1/film-texts/search/title?title=academy
    //   → returns all films with "academy" in the title
    // ────────────────────────────────────────────────────────────────
    @GetMapping("/search/title")
    public ResponseEntity<List<FilmTextResponseDTO>> getByTitle(
            @RequestParam String title) {
        return ResponseEntity.ok(filmTextService.getByTitle(title));
    }
 
 
    // ────────────────────────────────────────────────────────────────
    // GET /api/v1/film-texts/search/description?description=epic
    //
    // Returns all film_text records whose description contains
    // the given keyword (case-insensitive partial match)
    //
    // Example:
    //   GET http://localhost:8082/api/v1/film-texts/search/description?description=epic
    //   → returns all films whose description contains "epic"
    // ────────────────────────────────────────────────────────────────
    @GetMapping("/search/description")
    public ResponseEntity<List<FilmTextResponseDTO>> getByDescription(
            @RequestParam String description) {
        return ResponseEntity.ok(filmTextService.getByDescription(description));
    }
 
 
    // ────────────────────────────────────────────────────────────────
    // GET /api/v1/film-texts/search/keyword?keyword=drama
    //
    // Returns all film_text records where EITHER title OR description
    // contains the given keyword (case-insensitive)
    //
    // Difference from /search/title and /search/description:
    //   /search/title       → searches title column only
    //   /search/description → searches description column only
    //   /search/keyword     → searches BOTH columns at once
    //
    // Example:
    //   GET http://localhost:8082/api/v1/film-texts/search/keyword?keyword=drama
    //   → returns films where title OR description contains "drama"
    // ────────────────────────────────────────────────────────────────
    @GetMapping("/search/keyword")
    public ResponseEntity<List<FilmTextResponseDTO>> getByKeyword(
            @RequestParam String keyword) {
        return ResponseEntity.ok(filmTextService.getByKeyword(keyword));
    }

    // ────────────────────────────────────────────────────────────────
    // POST
    // ────────────────────────────────────────────────────────────────

    // POST /api/v1/film-texts
    // Body: { "filmId": 1, "title": "ACADEMY DINOSAUR", "description": "..." }
    // NOTE: Normally film_text is auto-populated by the Sakila ins_film trigger.
    // Use this endpoint only when manual insertion is needed.
    @PostMapping
    public ResponseEntity<FilmTextResponseDTO> create(
            @Valid @RequestBody FilmTextDTO filmTextDTO) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(filmTextService.create(filmTextDTO));
    }

    // ────────────────────────────────────────────────────────────────
    // PUT — full replace of title + description
    // ────────────────────────────────────────────────────────────────

    // PUT /api/v1/film-texts/{filmId}
    // Body: { "filmId": 1, "title": "NEW TITLE", "description": "New description" }
    @PutMapping("/{filmId}")
    public ResponseEntity<FilmTextResponseDTO> replace(
            @PathVariable Integer filmId,
            @Valid @RequestBody FilmTextDTO filmTextDTO) {
        return ResponseEntity.ok(filmTextService.replace(filmId, filmTextDTO));
    }
}