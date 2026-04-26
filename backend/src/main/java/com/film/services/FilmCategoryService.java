package com.film.services;

import com.film.dto.FilmCategoryDTO;
import com.film.dto.FilmCategoryResponseDTO;
import com.film.entity.Category;
import com.film.entity.Film;
import com.film.entity.FilmCategory;
import com.film.entity.FilmCategoryId;
import com.film.exception.DuplicateResourceException;
import com.film.exception.ResourceNotFoundException;
import com.film.repository.CategoryRepository;
import com.film.repository.FilmCategoryRepository;
import com.film.repository.FilmRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FilmCategoryService {

    private final FilmCategoryRepository filmCategoryRepository;
    private final FilmRepository         filmRepository;
    private final CategoryRepository     categoryRepository;


    // ── Helper: entity → response DTO ───────────────────────────────
    private FilmCategoryResponseDTO toResponse(FilmCategory fc) {
        return FilmCategoryResponseDTO.builder()
                .filmId(fc.getId().getFilmId())
                .categoryId(fc.getId().getCategoryId())
                .filmTitle(fc.getFilm().getTitle())
                .categoryName(fc.getCategory().getName())
                .filmRating(fc.getFilm().getRating() != null
                        ? fc.getFilm().getRating() : null)
                .releaseYear(fc.getFilm().getReleaseYear())
                .lastUpdate(fc.getLastUpdate())
                .build();
    }

    // ── Helper: validate and fetch Film ─────────────────────────────
    private Film fetchFilm(Integer filmId) {
        return filmRepository.findById(filmId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Film", "filmId", filmId));
    }

    // ── Helper: validate and fetch Category ─────────────────────────
    private Category fetchCategory(Integer categoryId) {
        return categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Category", "categoryId", categoryId));
    }


    // ── GET all paginated ────────────────────────────────────────────
    @Transactional(readOnly = true)
    public Page<FilmCategoryResponseDTO> findAllPaginated(int page, int size) {
        return filmCategoryRepository.findAll(PageRequest.of(page, size))
                .map(this::toResponse);
    }

    // ── GET all ──────────────────────────────────────────────────────
    @Transactional(readOnly = true)
    public List<FilmCategoryResponseDTO> getAll() {
        return filmCategoryRepository.findAll()
                .stream().map(this::toResponse).toList();
    }

    // ── GET by composite key ─────────────────────────────────────────
    @Transactional(readOnly = true)
    public FilmCategoryResponseDTO getById(Integer filmId, Integer categoryId) {
        FilmCategoryId key = new FilmCategoryId(filmId, categoryId);
        return toResponse(filmCategoryRepository.findById(key)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "FilmCategory", "filmId + categoryId",
                        filmId + " + " + categoryId)));
    }

    // ── GET all categories for a film ────────────────────────────────
    @Transactional(readOnly = true)
    public List<FilmCategoryResponseDTO> getByFilm(Integer filmId) {
        if (!filmRepository.existsById(filmId))
            throw new ResourceNotFoundException("Film", "filmId", filmId);
        return filmCategoryRepository.findByFilm_FilmId(filmId)
                .stream().map(this::toResponse).toList();
    }

    // ── GET all films for a category ─────────────────────────────────
    @Transactional(readOnly = true)
    public List<FilmCategoryResponseDTO> getByCategory(Integer categoryId) {
        if (!categoryRepository.existsById(categoryId))
            throw new ResourceNotFoundException("Category", "categoryId", categoryId);
        return filmCategoryRepository.findByCategory_CategoryId(categoryId)
                .stream().map(this::toResponse).toList();
    }

    // ── GET by category name ─────────────────────────────────────────
    @Transactional(readOnly = true)
    public List<FilmCategoryResponseDTO> getByCategoryName(String categoryName) {
        return filmCategoryRepository.findByCategoryName(categoryName)
                .stream().map(this::toResponse).toList();
    }

    // ── GET by category + film rating ────────────────────────────────
    @Transactional(readOnly = true)
    public List<FilmCategoryResponseDTO> getByCategoryAndRating(
            Integer categoryId, Film rating) {
        if (!categoryRepository.existsById(categoryId))
            throw new ResourceNotFoundException("Category", "categoryId", categoryId);
        return filmCategoryRepository
                .findByCategoryIdAndFilmRating(categoryId, rating)
                .stream().map(this::toResponse).toList();
    }

    // ── GET count of films in a category ─────────────────────────────
    @Transactional(readOnly = true)
    public long countFilmsInCategory(Integer categoryId) {
        if (!categoryRepository.existsById(categoryId))
            throw new ResourceNotFoundException("Category", "categoryId", categoryId);
        return filmCategoryRepository.countByCategory_CategoryId(categoryId);
    }

    // ── GET count of categories for a film ───────────────────────────
    @Transactional(readOnly = true)
    public long countCategoriesForFilm(Integer filmId) {
        if (!filmRepository.existsById(filmId))
            throw new ResourceNotFoundException("Film", "filmId", filmId);
        return filmCategoryRepository.countByFilm_FilmId(filmId);
    }


    // ── POST — assign a category to a film ───────────────────────────
    @Transactional
    public FilmCategoryResponseDTO create(FilmCategoryDTO dto) {
        FilmCategoryId key = new FilmCategoryId(dto.getFilmId(), dto.getCategoryId());

        // Guard: prevent duplicate (filmId, categoryId) assignment
        if (filmCategoryRepository.existsById(key)) {
            throw new DuplicateResourceException(
                    "FilmCategory");
        }

        Film     film     = fetchFilm(dto.getFilmId());
        Category category = fetchCategory(dto.getCategoryId());

        FilmCategory fc = FilmCategory.builder()
                .id(key)
                .film(film)
                .category(category)
                .lastUpdate(LocalDateTime.now())
                .build();

        return toResponse(filmCategoryRepository.save(fc));
    }


    // ── PUT — replace (change category for a film) ───────────────────
    // Identifies the existing row by (filmId, categoryId) path variables,
    // deletes it, and inserts a new row with the new (filmId, categoryId).
    @Transactional
    public FilmCategoryResponseDTO replace(
            Integer filmId, Integer categoryId, FilmCategoryDTO dto) {

        FilmCategoryId oldKey = new FilmCategoryId(filmId, categoryId);
        FilmCategoryId newKey = new FilmCategoryId(dto.getFilmId(), dto.getCategoryId());

        // Load existing record
        FilmCategory existing = filmCategoryRepository.findById(oldKey)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "FilmCategory", "filmId + categoryId",
                        filmId + " + " + categoryId));

        // If key changes, check the new key doesn't already exist
        if (!oldKey.equals(newKey) && filmCategoryRepository.existsById(newKey)) {
            throw new DuplicateResourceException(
                    "FilmCategory");
        }

        Film     newFilm     = fetchFilm(dto.getFilmId());
        Category newCategory = fetchCategory(dto.getCategoryId());

        // Composite key change: delete old row, insert new one
        filmCategoryRepository.delete(existing);
        filmCategoryRepository.flush();

        FilmCategory updated = FilmCategory.builder()
                .id(newKey)
                .film(newFilm)
                .category(newCategory)
                .lastUpdate(LocalDateTime.now())
                .build();

        return toResponse(filmCategoryRepository.save(updated));
    }
}