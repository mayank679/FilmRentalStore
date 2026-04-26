package com.film.controller;

import com.film.dto.FilmActorDTO;
import com.film.dto.FilmActorResponseDTO;
import com.film.services.FilmActorService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import org.springframework.data.domain.Page;

@RestController
@RequestMapping("/api/filmactor")
@RequiredArgsConstructor
public class FilmActorController {

    private final FilmActorService filmActorService;

    // GET /api/filmactor/paged?page=0&size=10
    @GetMapping("/paged")
    public ResponseEntity<Page<FilmActorResponseDTO>> getAllPaged(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(filmActorService.findAllPaginated(page, size));
    }

    // GET /api/v1/film-actors
    // GET /api/v1/film-actors?filmId=1
    // GET /api/v1/film-actors?actorId=5
    @GetMapping
    public ResponseEntity<List<FilmActorResponseDTO>> getAll(
            @RequestParam(required = false) Integer filmId,
            @RequestParam(required = false) Integer actorId) {

        if (filmId  != null) return ResponseEntity.ok(filmActorService.getByFilm(filmId));
        if (actorId != null) return ResponseEntity.ok(filmActorService.getByActor(actorId));
        return ResponseEntity.ok(filmActorService.getAll());
    }

    // GET /api/v1/film-actors/{actorId}/{filmId}
    @GetMapping("/{actorId}/{filmId}")
    public ResponseEntity<FilmActorResponseDTO> getById(
            @PathVariable Integer actorId,
            @PathVariable Integer filmId) {
        return ResponseEntity.ok(filmActorService.getById(actorId, filmId));
    }

    // POST /api/filmactor
    @PostMapping
    public ResponseEntity<FilmActorResponseDTO> create(
            @Valid @RequestBody FilmActorDTO filmActorDTO) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(filmActorService.create(filmActorDTO));
    }

    // PUT /api/filmactor/{actorId}/{filmId}
    @PutMapping("/{actorId}/{filmId}")
    public ResponseEntity<FilmActorResponseDTO> replace(
            @PathVariable Integer actorId,
            @PathVariable Integer filmId,
            @Valid @RequestBody FilmActorDTO filmActorDTO) {
        return ResponseEntity.ok(filmActorService.replace(actorId, filmId, filmActorDTO));
    }
}