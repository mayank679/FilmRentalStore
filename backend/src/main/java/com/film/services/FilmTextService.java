package com.film.services;

import com.film.dto.FilmTextDTO;
import com.film.dto.FilmTextDetailResponseDTO;
import com.film.dto.FilmTextResponseDTO;
import com.film.entity.Film;
import com.film.entity.FilmText;
import com.film.exception.DuplicateResourceException;
import com.film.exception.ResourceNotFoundException;
import com.film.repository.FilmRepository;
import com.film.repository.FilmTextRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FilmTextService {

    private final FilmTextRepository filmTextRepository;
    private final FilmRepository     filmRepository;



    // ── Helper: entity → basic response DTO ─────────────────────────
    private FilmTextResponseDTO toBasicResponse(FilmText ft) {
        return FilmTextResponseDTO.builder()
                .filmId(ft.getFilmId())
                .title(ft.getTitle())
                .description(ft.getDescription())
                .build();
    }
 
 
    // ── Helper: entity → full detail response DTO ───────────────────
    // Resolves Film + Language via the FK relationship
    private FilmTextDetailResponseDTO toDetailResponse(FilmText ft) {
        Film film = ft.getFilm();

        return FilmTextDetailResponseDTO.builder()
                .filmId(ft.getFilmId())
                .title(ft.getTitle())
                .description(ft.getDescription())
                .releaseYear(film.getReleaseYear())
                .language(film.getLanguage() != null
                        ? film.getLanguage().getName() : null)
                .originalLanguage(film.getOriginalLanguage() != null
                        ? film.getOriginalLanguage().getName() : null)
                .rentalDuration(film.getRentalDuration())
                .rentalRate(film.getRentalRate())
                .length(film.getLength())
                .replacementCost(film.getReplacementCost())
                .rating(film.getRating())           // ← just pass it directly
                .specialFeatures(film.getSpecialFeatures())
                .lastUpdate(film.getLastUpdate())
                .build();
    }
 
    // ── GET all film_text records ────────────────────────────────────
    // Returns only film_text fields (filmId, title, description)
    @Transactional(readOnly = true)
    public Page<FilmTextResponseDTO> findAllPaginated(int page, int size) {
        return filmTextRepository.findAll(PageRequest.of(page, size))
                .map(this::toBasicResponse);
    }

    public List<FilmTextResponseDTO> getAll() {
        return filmTextRepository.findAll()
                .stream()
                .map(this::toBasicResponse)
                .toList();
    }
 
 
    // ── GET film_text + full film details by filmId ──────────────────
    // film_id is the FK linking film_text → film
    // Returns all film_text fields + all Film fields joined
 // FilmTextService.java
    @Transactional(readOnly = true)
    public FilmTextDetailResponseDTO getByFilmIdWithDetails(Integer filmId) {

        // ✅ Must use findByFilmIdWithDetails — NOT findById
        FilmText ft = filmTextRepository.findByFilmIdWithDetails(filmId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "FilmText", "filmId", filmId));

        return toDetailResponse(ft);   // ft.getFilm() now works — eagerly loaded
    }
 
    // ── GET film_text records by title keyword ───────────────────────
    // Partial, case-insensitive match on title column
    @Transactional(readOnly = true)
    public List<FilmTextResponseDTO> getByTitle(String title) {
        return filmTextRepository
                .findByTitleContainingIgnoreCase(title)
                .stream()
                .map(this::toBasicResponse)
                .toList();
    }
 
 
    // ── GET film_text records by description keyword ─────────────────
    // Partial, case-insensitive match on description column
    @Transactional(readOnly = true)
    public List<FilmTextResponseDTO> getByDescription(String description) {
        return filmTextRepository
                .findByDescriptionContainingIgnoreCase(description)
                .stream()
                .map(this::toBasicResponse)
                .toList();
    }
 
 
    // ── GET film_text records by keyword ─────────────────────────────
    // Searches BOTH title AND description columns
    // Returns records where either field contains the keyword
    @Transactional(readOnly = true)
    public List<FilmTextResponseDTO> getByKeyword(String keyword) {
        return filmTextRepository
                .searchByKeyword(keyword)
                .stream()
                .map(this::toBasicResponse)
                .toList();
    }
    
    @Transactional
    public FilmTextResponseDTO create(FilmTextDTO dto) {
 
        // Guard: film must exist
        Film film = filmRepository.findById(dto.getFilmId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Film", "filmId", dto.getFilmId()));
 
        // Guard: film_text record must not already exist
        if (filmTextRepository.existsByFilmId(dto.getFilmId())) {
            throw new DuplicateResourceException(
                    "FilmText");
        }
 
        FilmText filmText = FilmText.builder()
                .filmId(dto.getFilmId())   // manually set — no @GeneratedValue
                .title(dto.getTitle())
                .description(dto.getDescription())
                .film(film)
                .build();
 
        return toBasicResponse(filmTextRepository.save(filmText));
    }
 
 
    // ── PUT — full replace of title and description ──────────────────
    // NOTE: In Sakila the upd_film trigger syncs film_text automatically
    // when film.title or film.description changes.
    // This endpoint allows direct update of film_text independently.
    @Transactional
    public FilmTextResponseDTO replace(Integer filmId, FilmTextDTO dto) {
 
        FilmText existing = filmTextRepository.findById(filmId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "FilmText", "filmId", filmId));
 
        existing.setTitle(dto.getTitle());
        existing.setDescription(dto.getDescription());
 
        return toBasicResponse(filmTextRepository.save(existing));
    }

    
}
