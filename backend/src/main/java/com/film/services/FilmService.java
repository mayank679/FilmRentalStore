package com.film.services;

import com.film.dto.CategoryCountDTO;
import com.film.dto.FilmDTO;
import com.film.dto.FilmPatchDTO;
import com.film.dto.FilmRequestDTO;
import com.film.entity.*;
import com.film.exception.CategoryNotFoundException;
import com.film.exception.EmptyResultException;
import com.film.exception.FilmNotFoundException;
import com.film.exception.LanguageNotFoundException;
import com.film.repository.CategoryRepository;
import com.film.repository.FilmCategoryRepository;
import com.film.repository.FilmRepository;
import com.film.repository.LanguageRepository;
import jakarta.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class FilmService {

    @Autowired
    private FilmRepository filmRepository;

    @Autowired
    private LanguageRepository languageRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private FilmCategoryRepository filmCategoryRepository;

    @Autowired
    private EntityManager entityManager;

    // ─── DTO Mapper ──────────────────────────────────────────────────────────


    public FilmDTO toDTO(Film film) {

        List<String> categoryNames = film.getFilmCategories() != null
                ? film.getFilmCategories().stream()
                .map(fc -> fc.getCategory().getName())
                .toList()
                : null;

        return FilmDTO.builder()
                .filmId(film.getFilmId())
                .title(film.getTitle())
                .description(film.getDescription())
                .releaseYear(film.getReleaseYear())
                .languageName(film.getLanguage() != null ? film.getLanguage().getName() : null)
                .rentalDuration(film.getRentalDuration())
                .rentalRate(film.getRentalRate())
                .length(film.getLength())
                .replacementCost(film.getReplacementCost())
                .rating(film.getRating())
                .specialFeatures(film.getSpecialFeatures())
                .categoryNames(categoryNames)   //FIX
                .build();
    }

    // ─── Shared helper: resolve Language by ID ────────────────────────────────

    private Language resolveLanguage(Integer languageId) {
        return languageRepository.findById(languageId)
                .orElseThrow(() -> new LanguageNotFoundException(languageId));
    }

    // ─── Shared helper: resolve Category by ID ───────────────────────────────

    private Category resolveCategory(Integer categoryId) {
        return categoryRepository.findById(categoryId)
                .orElseThrow(() -> new CategoryNotFoundException(categoryId));
    }

    // ─── Shared helper: assign categories to a Film ──────────────────────────
    // Deletes existing links first, then re-creates them.
    // Call inside a @Transactional method only.

    private void assignCategories(Film film, List<Integer> categoryIds) {
        filmCategoryRepository.deleteByFilmId(film.getFilmId());

        if (categoryIds != null && !categoryIds.isEmpty()) {
            for (Integer categoryId : categoryIds) {
                Category category = resolveCategory(categoryId);

                FilmCategoryId fcId = new FilmCategoryId(film.getFilmId(), categoryId);

                FilmCategory fc = FilmCategory.builder()
                        .id(fcId)
                        .film(film)
                        .category(category)
                        .build();

                filmCategoryRepository.save(fc);
            }
        }
    }


    // ─── Existing GET methods (unchanged) ────────────────────────────────────

    public List<FilmDTO> getFirst10Films() {
        List<Film> films = filmRepository.findAll(PageRequest.of(0, 10)).getContent();
        if (films.isEmpty()) throw new EmptyResultException("No films found in the database.");
        return films.stream().map(this::toDTO).toList();
    }

    public Page<FilmDTO> findAllPaginated(int page, int size) {
        return filmRepository.findAll(PageRequest.of(page, size))
                .map(this::toDTO);
    }

    public List<FilmDTO> getAllFilms() {
        List<Film> films = filmRepository.findAll();
        if (films.isEmpty()) throw new EmptyResultException("No films found in the database.");
        return films.stream().map(this::toDTO).toList();
    }

    public FilmDTO getFilmById(Integer id) {
        Film film = filmRepository.findById(id)
                .orElseThrow(() -> new FilmNotFoundException(id));
        return toDTO(film);
    }

    public List<FilmDTO> getFilmsByLanguageName(String name) {
        List<Film> films = filmRepository.findByLanguage_Name(name);
        if (films.isEmpty()) throw new EmptyResultException("language name", name);
        return films.stream().map(this::toDTO).toList();
    }

    public List<FilmDTO> getFilmsByRating(String rating) {
        List<Film> films = filmRepository.findByRating(rating);
        if (films.isEmpty()) throw new EmptyResultException("rating", rating);
        return films.stream().map(this::toDTO).toList();
    }

    public List<FilmDTO> getFilmByTitle(String title) {
        List<Film> films = filmRepository.findByTitle(title);
        if (films.isEmpty()) throw new EmptyResultException("title", title);
        return films.stream().map(this::toDTO).toList();
    }



    public List<FilmDTO> getFilmByReleaseYear(int releaseYear) {
        List<Film> films = filmRepository.findByReleaseYear(releaseYear);
        if (films.isEmpty()) throw new EmptyResultException("release year", String.valueOf(releaseYear));
        return films.stream().map(this::toDTO).toList();
    }

    public List<FilmDTO> getFilmsByLanguageId(Integer languageId) {
        List<Film> films = filmRepository.findByLanguage_LanguageId(languageId);
        if (films.isEmpty()) throw new EmptyResultException("language ID", String.valueOf(languageId));
        return films.stream().map(this::toDTO).toList();
    }

    public List<FilmDTO> getFilmByRentalDuration(int rentalDuration) {
        List<Film> films = filmRepository.findByRentalDuration(rentalDuration);
        if (films.isEmpty()) throw new EmptyResultException("rental duration", rentalDuration + " days");
        return films.stream().map(this::toDTO).toList();
    }

    public List<FilmDTO> getFilmsByRentalRate(BigDecimal rentalRate) {
        List<Film> films = filmRepository.findByRentalRate(rentalRate);
        if (films.isEmpty()) throw new EmptyResultException("rental rate", String.valueOf(rentalRate));
        return films.stream().map(this::toDTO).toList();
    }

    public List<FilmDTO> getFilmsByLength(Integer length) {
        List<Film> films = filmRepository.findByLength(length);
        if (films.isEmpty()) throw new EmptyResultException("length", length + " minutes");
        return films.stream().map(this::toDTO).toList();
    }

    public List<FilmDTO> getFilmsByReplacementCost(BigDecimal replacementCost) {
        List<Film> films = filmRepository.findByReplacementCost(replacementCost);
        if (films.isEmpty()) throw new EmptyResultException("replacement cost", String.valueOf(replacementCost));
        return films.stream().map(this::toDTO).toList();
    }

    public List<FilmDTO> getFilmsBySpecialFeatures(String specialFeatures) {
        List<Film> films = filmRepository.findBySpecialFeatures(specialFeatures);
        if (films.isEmpty()) throw new EmptyResultException("special features", specialFeatures);
        return films.stream().map(this::toDTO).toList();
    }



    //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    // 🔹 MAX / MIN rental rate
    public BigDecimal getMaxRentalRate() {
        return filmRepository.findMaxRentalRate();
    }

    public BigDecimal getMinRentalRate() {
        return filmRepository.findMinRentalRate();
    }

    // 🔹 MAX / MIN replacement cost
    public BigDecimal getMaxReplacementCost() {
        return filmRepository.findMaxReplacementCost();
    }

    public BigDecimal getMinReplacementCost() {
        return filmRepository.findMinReplacementCost();
    }

    // 🔹 RANGE rental rate
    public List<FilmDTO> getFilmsByRentalRateRange(BigDecimal min, BigDecimal max) {
        List<Film> films = filmRepository.findByRentalRateBetween(min, max);

        if (films.isEmpty()) {
            throw new RuntimeException("No films found in rental rate range");
        }

        return films.stream().map(this::toDTO).toList();
    }

    // 🔹 RANGE replacement cost
    public List<FilmDTO> getFilmsByReplacementCostRange(BigDecimal min, BigDecimal max) {
        List<Film> films = filmRepository.findByReplacementCostBetween(min, max);

        if (films.isEmpty()) {
            throw new RuntimeException("No films found in replacement cost range");
        }

        return films.stream().map(this::toDTO).toList();
    }

    //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~


    @Transactional
    public FilmDTO getFilmWithCategories(Integer filmId) {
        Film film = filmRepository.findById(filmId)
                .orElseThrow(() -> new FilmNotFoundException(filmId));
        List<String> categoryNames = filmRepository.findCategoryNamesByFilmId(filmId);
        if (categoryNames.isEmpty())
            throw new EmptyResultException("No categories found for film with ID: " + filmId);
        return FilmDTO.builder()
                .filmId(film.getFilmId()).title(film.getTitle())
                .description(film.getDescription()).releaseYear(film.getReleaseYear())
                .languageName(film.getLanguage() != null ? film.getLanguage().getName() : null)
                .rentalDuration(film.getRentalDuration()).rentalRate(film.getRentalRate())
                .length(film.getLength()).replacementCost(film.getReplacementCost())
                .rating(film.getRating()).specialFeatures(film.getSpecialFeatures())
                .categoryNames(categoryNames).build();
    }

    public List<CategoryCountDTO> getFilmCountByCategory() {
        List<CategoryCountDTO> result = filmRepository.countFilmsByCategory();
        if (result.isEmpty()) throw new EmptyResultException("No category data found.");
        return result;
    }

    public List<Object[]> getCountByRating() {
        List<Object[]> result = filmRepository.countByRating();
        if (result.isEmpty()) throw new EmptyResultException("No rating data found.");
        return result;
    }

    public List<Object[]> getCountByReleaseYear() {
        List<Object[]> result = filmRepository.countByReleaseYear();
        if (result.isEmpty()) throw new EmptyResultException("No release year data found.");
        return result;
    }

    public BigDecimal getAvgRentalRate() {
        BigDecimal result = filmRepository.avgRentalRate();
        if (result == null) throw new EmptyResultException("Could not calculate average rental rate. No films in database.");
        return result;
    }

    public Double getAvgLength() {
        Double result = filmRepository.avgLength();
        if (result == null) throw new EmptyResultException("Could not calculate average length. No films in database.");
        return result;
    }

    public BigDecimal getTotalReplacementCost() {
        BigDecimal result = filmRepository.totalReplacementCost();
        if (result == null) throw new EmptyResultException("Could not calculate total replacement cost. No films in database.");
        return result;
    }

    public Integer getTotalRentalDuration() {
        Integer result = filmRepository.totalRentalDuration();
        if (result == null) throw new EmptyResultException("Could not calculate total rental duration. No films in database.");
        return result;
    }

    public List<FilmDTO> getByIdAndRating(Integer filmId, String rating) {
        List<Film> films = filmRepository.findByFilmIdAndRating(filmId, rating);
        if (films.isEmpty()) throw new FilmNotFoundException(filmId, rating);
        return films.stream().map(this::toDTO).toList();
    }

    public List<FilmDTO> getByRatingAndRate(String rating, BigDecimal rentalRate) {
        List<Film> films = filmRepository.findByRatingAndRentalRate(rating, rentalRate);
        if (films.isEmpty()) throw new EmptyResultException("rating", rating, "rental rate", rentalRate);
        return films.stream().map(this::toDTO).toList();
    }

    public List<FilmDTO> getByLengthAndYear(Integer length, Integer releaseYear) {
        List<Film> films = filmRepository.findByLengthAndReleaseYear(length, releaseYear);
        if (films.isEmpty()) throw new EmptyResultException("length", length + " minutes", "release year", releaseYear);
        return films.stream().map(this::toDTO).toList();
    }

    // ─── POST: Create a new Film ──────────────────────────────────────────────



    @Transactional
    public FilmDTO createFilm(FilmRequestDTO request) {
        Language language = resolveLanguage(request.getLanguageId());
        Language originalLanguage = null;
        if (request.getOriginalLanguageId() != null) {
            originalLanguage = resolveLanguage(request.getOriginalLanguageId());
        }

        Film film = Film.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .releaseYear(request.getReleaseYear())
                .language(language)
                .originalLanguage(originalLanguage)
                .rentalDuration(request.getRentalDuration())
                .rentalRate(request.getRentalRate())
                .length(request.getLength())
                .replacementCost(request.getReplacementCost())
                .rating(request.getRating())
                .specialFeatures(request.getSpecialFeatures())
                .lastUpdate(LocalDateTime.now())
                .build();

        film = filmRepository.save(film);

        if (request.getCategoryIds() != null && !request.getCategoryIds().isEmpty()) {
            assignCategories(film, request.getCategoryIds());
        }

        // ✅ SINGLE FIX — reuse existing method that already handles categories correctly
        return getFilmWithCategories(film.getFilmId());
    }



    // ─── PUT: Full replace of an existing Film ────────────────────────────────
    // Replaces ALL fields. categoryIds replaces all category links.

    @Transactional
    public FilmDTO replaceFilm(Integer filmId, FilmRequestDTO request) {

        Film film = filmRepository.findById(filmId)
                .orElseThrow(() -> new FilmNotFoundException(filmId));

        Language language = resolveLanguage(request.getLanguageId());

        Language originalLanguage = null;
        if (request.getOriginalLanguageId() != null) {
            originalLanguage = resolveLanguage(request.getOriginalLanguageId());
        }

        film.setTitle(request.getTitle());
        film.setDescription(request.getDescription());
        film.setReleaseYear(request.getReleaseYear());
        film.setLanguage(language);
        film.setOriginalLanguage(originalLanguage);
        film.setRentalDuration(request.getRentalDuration());
        film.setRentalRate(request.getRentalRate());
        film.setLength(request.getLength());
        film.setReplacementCost(request.getReplacementCost());
        film.setRating(request.getRating());
        film.setSpecialFeatures(request.getSpecialFeatures());
        film.setLastUpdate(LocalDateTime.now());

        film = filmRepository.save(film);

        // Always replace category links on PUT (even if list is null/empty → clears all)
        assignCategories(film, request.getCategoryIds());

        return toDTO(film);
    }

    // ─── PATCH: Partial update of an existing Film ────────────────────────────
    // Only non-null fields in the request are applied.

    @Transactional
    public FilmDTO patchFilm(Integer filmId, FilmPatchDTO patch) {

        Film film = filmRepository.findById(filmId)
                .orElseThrow(() -> new FilmNotFoundException(filmId));

        if (patch.getTitle() != null) {
            film.setTitle(patch.getTitle());
        }
        if (patch.getDescription() != null) {
            film.setDescription(patch.getDescription());
        }
        if (patch.getReleaseYear() != null) {
            film.setReleaseYear(patch.getReleaseYear());
        }
        if (patch.getLanguageId() != null) {
            film.setLanguage(resolveLanguage(patch.getLanguageId()));
        }
        if (patch.getOriginalLanguageId() != null) {
            film.setOriginalLanguage(resolveLanguage(patch.getOriginalLanguageId()));
        }
        if (patch.getRentalDuration() != null) {
            film.setRentalDuration(patch.getRentalDuration());
        }
        if (patch.getRentalRate() != null) {
            film.setRentalRate(patch.getRentalRate());
        }
        if (patch.getLength() != null) {
            film.setLength(patch.getLength());
        }
        if (patch.getReplacementCost() != null) {
            film.setReplacementCost(patch.getReplacementCost());
        }
        if (patch.getRating() != null) {
            film.setRating(patch.getRating());
        }
        if (patch.getSpecialFeatures() != null) {
            film.setSpecialFeatures(patch.getSpecialFeatures());
        }

        film.setLastUpdate(LocalDateTime.now());
        film = filmRepository.save(film);

        // Only update categories if categoryIds is explicitly provided in the patch
        if (patch.getCategoryIds() != null) {
            assignCategories(film, patch.getCategoryIds());
        }

        return toDTO(film);
    }


}