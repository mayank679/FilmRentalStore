package com.film.controller;

import com.film.dto.FilmCategoryDTO;
import com.film.dto.FilmCategoryResponseDTO;
import com.film.entity.Film;
import com.film.services.FilmCategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import org.springframework.data.domain.Page;
import java.util.Map;

@RestController
@RequestMapping("/api/film-categories")
@RequiredArgsConstructor
public class FilmCategoryController {

    private final FilmCategoryService filmCategoryService;


    // ── GET /api/film-categories/paged?page=0&size=10
    @GetMapping("/paged")
    public ResponseEntity<Page<FilmCategoryResponseDTO>> getAllPaged(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(filmCategoryService.findAllPaginated(page, size));
    }

    // ── GET /api/v1/film-categories
    // ── GET /api/v1/film-categories?filmId=1
    // ── GET /api/v1/film-categories?categoryId=3
    // ── GET /api/v1/film-categories?categoryName=Action
    @GetMapping
    public ResponseEntity<List<FilmCategoryResponseDTO>> getAll(
            @RequestParam(required = false) Integer filmId,
            @RequestParam(required = false) Integer categoryId,
            @RequestParam(required = false) String  categoryName) {

        if (filmId != null)       return ResponseEntity.ok(filmCategoryService.getByFilm(filmId));
        if (categoryId != null)   return ResponseEntity.ok(filmCategoryService.getByCategory(categoryId));
        if (categoryName != null) return ResponseEntity.ok(filmCategoryService.getByCategoryName(categoryName));
        return ResponseEntity.ok(filmCategoryService.getAll());
    }

    // ── GET /api/film-categories/{filmId}/{categoryId}
    @GetMapping("/{filmId}/{categoryId}")
    public ResponseEntity<FilmCategoryResponseDTO> getById(
            @PathVariable Integer filmId,
            @PathVariable Integer categoryId) {
        return ResponseEntity.ok(filmCategoryService.getById(filmId, categoryId));
    }

    // ── GET /api/film-categories/category/{categoryId}/films
    //    All films in a specific category
    @GetMapping("/category/{categoryId}/films")
    public ResponseEntity<List<FilmCategoryResponseDTO>> getFilmsByCategory(
            @PathVariable Integer categoryId) {
        return ResponseEntity.ok(filmCategoryService.getByCategory(categoryId));
    }

    // ── GET /api/film-categories/film/{filmId}/categories
    //    All categories for a specific film
    @GetMapping("/film/{filmId}/categories")
    public ResponseEntity<List<FilmCategoryResponseDTO>> getCategoriesByFilm(
            @PathVariable Integer filmId) {
        return ResponseEntity.ok(filmCategoryService.getByFilm(filmId));
    }

    // ── GET /api/film-categories/category/{categoryId}/rating/{rating}
    //    Films in a category filtered by rating (G, PG, PG_13, R, NC_17)
    @GetMapping("/category/{categoryId}/rating/{rating}")
    public ResponseEntity<List<FilmCategoryResponseDTO>> getByRating(
            @PathVariable Integer categoryId,
            @PathVariable Film rating) {
        return ResponseEntity.ok(
                filmCategoryService.getByCategoryAndRating(categoryId, rating));
    }

    // ── GET /api/film-categories/category/{categoryId}/count
    //    How many films are in a category
    @GetMapping("/category/{categoryId}/count")
    public ResponseEntity<Map<String, Long>> countFilmsInCategory(
            @PathVariable Integer categoryId) {
        return ResponseEntity.ok(Map.of(
                "categoryId", categoryId.longValue(),
                "filmCount", filmCategoryService.countFilmsInCategory(categoryId)
        ));
    }

    // ── GET /api/film-categories/film/{filmId}/count
    //    How many categories a film belongs to
    @GetMapping("/film/{filmId}/count")
    public ResponseEntity<Map<String, Long>> countCategoriesForFilm(
            @PathVariable Integer filmId) {
        return ResponseEntity.ok(Map.of(
                "filmId", filmId.longValue(),
                "categoryCount", filmCategoryService.countCategoriesForFilm(filmId)
        ));
    }


    // ── POST /api/film-categories
    //    Body: { "filmId": 1, "categoryId": 3 }
    @PostMapping
    public ResponseEntity<FilmCategoryResponseDTO> create(
            @Valid @RequestBody FilmCategoryDTO filmCategoryDTO) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(filmCategoryService.create(filmCategoryDTO));
    }


    // ── PUT /api/film-categories/{filmId}/{categoryId}
    //    Full replace — change the film or category on an existing assignment
    //    Body: { "filmId": 1, "categoryId": 5 }
    @PutMapping("/{filmId}/{categoryId}")
    public ResponseEntity<FilmCategoryResponseDTO> replace(
            @PathVariable Integer filmId,
            @PathVariable Integer categoryId,
            @Valid @RequestBody FilmCategoryDTO filmCategoryDTO) {
        return ResponseEntity.ok(
                filmCategoryService.replace(filmId, categoryId, filmCategoryDTO));
    }


  
 
    }