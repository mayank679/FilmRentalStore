package com.film.controller;

import com.film.dto.CategoryCountDTO;
import com.film.dto.FilmDTO;
import com.film.dto.FilmPatchDTO;
import com.film.dto.FilmRequestDTO;
import com.film.exception.CategoryNotFoundException;
import com.film.exception.EmptyResultException;
import com.film.exception.FilmNotFoundException;
import com.film.exception.LanguageNotFoundException;
import com.film.services.FilmService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import org.springframework.data.domain.Page;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/films")
public class FilmController {

    @Autowired
    private FilmService filmService;

    // ─── Error builder ────────────────────────────────────────────────────────

    private ResponseEntity<Map<String, Object>> error(HttpStatus status, String message) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", LocalDateTime.now().toString());
        body.put("status", status.value());
        body.put("error", status.getReasonPhrase());
        body.put("message", message);
        return ResponseEntity.status(status).body(body);
    }

    // ─── Existing GET endpoints (unchanged) ───────────────────────────────────

    @GetMapping("/first10")
    public ResponseEntity<?> getFirst10Films() {
        try {
            return ResponseEntity.ok(filmService.getFirst10Films());
        } catch (EmptyResultException e) {
            return error(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    // GET all films (paginated)
    @GetMapping("/paged")
    public ResponseEntity<Page<FilmDTO>> getAllFilmsPaged(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(filmService.findAllPaginated(page, size));
    }

    @GetMapping
    public ResponseEntity<?> getAllFilms() {
        try {
            return ResponseEntity.ok(filmService.getAllFilms());
        } catch (EmptyResultException e) {
            return error(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getFilmById(@PathVariable Integer id) {
        try {
            return ResponseEntity.ok(filmService.getFilmById(id));
        } catch (FilmNotFoundException e) {
            return error(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @GetMapping("/by-language-name")
    public ResponseEntity<?> getFilmsByLanguageName(@RequestParam String name) {
        try {
            return ResponseEntity.ok(filmService.getFilmsByLanguageName(name));
        } catch (EmptyResultException e) {
            return error(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }
//REMOVE
    @GetMapping("/by-rating")
    public ResponseEntity<?> getFilmsByRating(@RequestParam String rating) {
        try {
            return ResponseEntity.ok(filmService.getFilmsByRating(rating));
        } catch (EmptyResultException e) {
            return error(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @GetMapping("/by-title")
    public ResponseEntity<?> getFilsByTitle(@RequestParam String title) {
        try {
            return ResponseEntity.ok(filmService.getFilmByTitle(title));
        } catch (EmptyResultException e) {
            return error(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @GetMapping("/by-release-year")
    public ResponseEntity<?> getFilsByReleaseYear(@RequestParam int releaseYear) {
        try {
            return ResponseEntity.ok(filmService.getFilmByReleaseYear(releaseYear));
        } catch (EmptyResultException e) {
            return error(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @GetMapping("/by-language-id")
    public ResponseEntity<?> getFilmsByLanguageId(@RequestParam Integer languageId) {
        try {
            return ResponseEntity.ok(filmService.getFilmsByLanguageId(languageId));
        } catch (EmptyResultException e) {
            return error(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }
    //DELETE
    @GetMapping("/by-rental-duration")
    public ResponseEntity<?> getFilmsByRentalDuration(@RequestParam int rentalDuration) {
        try {
            return ResponseEntity.ok(filmService.getFilmByRentalDuration(rentalDuration));
        } catch (EmptyResultException e) {
            return error(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }
//DELETE
    @GetMapping("/by-rental-rate")
    public ResponseEntity<?> getFilmsByRentalRate(@RequestParam BigDecimal rentalRate) {
        try {
            return ResponseEntity.ok(filmService.getFilmsByRentalRate(rentalRate));
        } catch (EmptyResultException e) {
            return error(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }
//DELETE
    @GetMapping("/{filmId}/categories")
    public ResponseEntity<?> getFilmWithCategories(@PathVariable Integer filmId) {
        try {
            return ResponseEntity.ok(filmService.getFilmWithCategories(filmId));
        } catch (FilmNotFoundException | EmptyResultException e) {
            return error(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @GetMapping("/count-by-category")
    public ResponseEntity<?> getFilmCountByCategory() {
        try {
            return ResponseEntity.ok(filmService.getFilmCountByCategory());
        } catch (EmptyResultException e) {
            return error(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @GetMapping("/count-by-rating")
    public ResponseEntity<?> getCountByRating() {
        try {
            return ResponseEntity.ok(filmService.getCountByRating());
        } catch (EmptyResultException e) {
            return error(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @GetMapping("/count-by-release-year")
    public ResponseEntity<?> getCountByReleaseYear() {
        try {
            return ResponseEntity.ok(filmService.getCountByReleaseYear());
        } catch (EmptyResultException e) {
            return error(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @GetMapping("/avg-rental-rate")
    public ResponseEntity<?> getAvgRentalRate() {
        try {
            return ResponseEntity.ok(filmService.getAvgRentalRate());
        } catch (EmptyResultException e) {
            return error(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @GetMapping("/avg-length")
    public ResponseEntity<?> getAvgLength() {
        try {
            return ResponseEntity.ok(filmService.getAvgLength());
        } catch (EmptyResultException e) {
            return error(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @GetMapping("/total-replacement-cost")
    public ResponseEntity<?> getTotalReplacementCost() {
        try {
            return ResponseEntity.ok(filmService.getTotalReplacementCost());
        } catch (EmptyResultException e) {
            return error(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @GetMapping("/total-rental-duration")
    public ResponseEntity<?> getTotalRentalDuration() {
        try {
            return ResponseEntity.ok(filmService.getTotalRentalDuration());
        } catch (EmptyResultException e) {
            return error(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    //DELETE
    @GetMapping("/by-id-and-rating")
    public ResponseEntity<?> getByIdAndRating(@RequestParam Integer filmId,
                                              @RequestParam String rating) {
        try {
            return ResponseEntity.ok(filmService.getByIdAndRating(filmId, rating));
        } catch (FilmNotFoundException e) {
            return error(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @GetMapping("/by-rating-and-rate")
    public ResponseEntity<?> getByRatingAndRate(@RequestParam String rating,
                                                @RequestParam BigDecimal rentalRate) {
        try {
            return ResponseEntity.ok(filmService.getByRatingAndRate(rating, rentalRate));
        } catch (EmptyResultException e) {
            return error(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @GetMapping("/by-length-and-year")
    public ResponseEntity<?> getByLengthAndYear(@RequestParam Integer length,
                                                @RequestParam Integer releaseYear) {
        try {
            return ResponseEntity.ok(filmService.getByLengthAndYear(length, releaseYear));
        } catch (EmptyResultException e) {
            return error(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    // 🔹 MAX / MIN rental rate
    @GetMapping("/max-rental-rate")
    public ResponseEntity<?> getMaxRentalRate() {
        return ResponseEntity.ok(filmService.getMaxRentalRate());
    }

    @GetMapping("/min-rental-rate")
    public ResponseEntity<?> getMinRentalRate() {
        return ResponseEntity.ok(filmService.getMinRentalRate());
    }

    // 🔹 MAX / MIN replacement cost
    @GetMapping("/max-replacement-cost")
    public ResponseEntity<?> getMaxReplacementCost() {
        return ResponseEntity.ok(filmService.getMaxReplacementCost());
    }

    @GetMapping("/min-replacement-cost")
    public ResponseEntity<?> getMinReplacementCost() {
        return ResponseEntity.ok(filmService.getMinReplacementCost());
    }

    // 🔹 RANGE rental rate
    @GetMapping("/rental-rate-range")
    public ResponseEntity<?> getFilmsByRentalRateRange(
            @RequestParam BigDecimal min,
            @RequestParam BigDecimal max) {

        return ResponseEntity.ok(
                filmService.getFilmsByRentalRateRange(min, max)
        );
    }

    // 🔹 RANGE replacement cost
    @GetMapping("/replacement-cost-range")
    public ResponseEntity<?> getFilmsByReplacementCostRange(
            @RequestParam BigDecimal min,
            @RequestParam BigDecimal max) {

        return ResponseEntity.ok(
                filmService.getFilmsByReplacementCostRange(min, max)
        );
    }
    //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    // ─── POST: Create a new Film ──────────────────────────────────────────────

   /* @PostMapping
    public ResponseEntity<?> createFilm(@Valid @RequestBody FilmRequestDTO request) {
        try {
            FilmDTO created = filmService.createFilm(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (LanguageNotFoundException | CategoryNotFoundException e) {
            return error(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }*/


    @PostMapping
    public ResponseEntity<?> createFilm(@RequestBody @Valid FilmRequestDTO request)
    {
        try{
            FilmDTO created=filmService.createFilm(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        }
        catch(LanguageNotFoundException | CategoryNotFoundException e)
        {
            return error(HttpStatus.NOT_FOUND,e.getMessage());
        }

    }






    // ─── PUT: Full replace ────────────────────────────────────────────────────

    @PutMapping("/{id}")
    public ResponseEntity<?> replaceFilm(@PathVariable Integer id,
                                         @Valid @RequestBody FilmRequestDTO request) {
        try {
            FilmDTO updated = filmService.replaceFilm(id, request);
            return ResponseEntity.ok(updated);
        } catch (FilmNotFoundException | LanguageNotFoundException | CategoryNotFoundException e) {
            return error(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    // ─── PATCH: Partial update ────────────────────────────────────────────────

    @PatchMapping("/{id}")
    public ResponseEntity<?> patchFilm(@PathVariable Integer id,
                                       @Valid @RequestBody FilmPatchDTO patch) {
        try {
            FilmDTO updated = filmService.patchFilm(id, patch);
            return ResponseEntity.ok(updated);
        } catch (FilmNotFoundException | LanguageNotFoundException | CategoryNotFoundException e) {
            return error(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    
}