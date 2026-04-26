package com.film.service;

import com.film.services.FilmService;
import com.film.dto.FilmDTO;
import com.film.dto.FilmRequestDTO;
import com.film.entity.Film;
import com.film.entity.Language;
import com.film.exception.FilmNotFoundException;
import com.film.exception.LanguageNotFoundException;
import com.film.repository.CategoryRepository;
import com.film.repository.FilmCategoryRepository;
import com.film.repository.FilmRepository;
import com.film.repository.LanguageRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FilmServiceTest {

    @Mock
    private FilmRepository filmRepository;

    @Mock
    private LanguageRepository languageRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private FilmCategoryRepository filmCategoryRepository;

    @Mock
    private EntityManager entityManager;

    @InjectMocks
    private FilmService filmService;

    @Test
    void testGetAllFilms() {
        Film film = new Film();
        film.setFilmId(1);

        when(filmRepository.findAll()).thenReturn(List.of(film));

        List<FilmDTO> result = filmService.getAllFilms();

        assertThat(result).hasSize(1);
    }

    @Test
    void testGetFilmById() {
        Film film = new Film();
        film.setFilmId(1);

        when(filmRepository.findById(1)).thenReturn(Optional.of(film));

        FilmDTO result = filmService.getFilmById(1);

        assertThat(result.getFilmId()).isEqualTo(1);
    }

    @Test
    void testGetFilmById_NotFound() {
        when(filmRepository.findById(1)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> filmService.getFilmById(1))
                .isInstanceOf(FilmNotFoundException.class);
    }

    @Test
    void testGetFilmsByRating() {
        Film film = new Film();
        film.setRating("PG");

        when(filmRepository.findByRating("PG")).thenReturn(List.of(film));

        List<FilmDTO> result = filmService.getFilmsByRating("PG");

        assertThat(result).isNotEmpty();
    }

    @Test
    void testCreateFilm() {
        FilmRequestDTO request = new FilmRequestDTO();
        request.setTitle("Test Film");
        request.setLanguageId(1);
        request.setRentalRate(BigDecimal.valueOf(2.99));

        Language language = new Language();
        language.setLanguageId((byte) 1);

        Film saved = new Film();
        saved.setFilmId(1);
        saved.setLanguage(language);

        when(languageRepository.findById(1)).thenReturn(Optional.of(language));
        when(filmRepository.save(any(Film.class))).thenReturn(saved);
        when(filmRepository.findById(1)).thenReturn(Optional.of(saved));
        when(filmRepository.findCategoryNamesByFilmId(1)).thenReturn(List.of("Action"));

        FilmDTO result = filmService.createFilm(request);

        assertThat(result).isNotNull();
        assertThat(result.getFilmId()).isEqualTo(1);
    }

    @Test
    void testCreateFilm_LanguageNotFound() {
        FilmRequestDTO request = new FilmRequestDTO();
        request.setLanguageId(1);

        when(languageRepository.findById(1)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> filmService.createFilm(request))
                .isInstanceOf(LanguageNotFoundException.class);
    }

    @Test
    void testReplaceFilm() {
        Film film = new Film();
        film.setFilmId(1);

        FilmRequestDTO request = new FilmRequestDTO();
        request.setLanguageId(1);

        Language language = new Language();
        language.setLanguageId((byte) 1);

        when(filmRepository.findById(1)).thenReturn(Optional.of(film));
        when(languageRepository.findById(1)).thenReturn(Optional.of(language));
        when(filmRepository.save(film)).thenReturn(film);

        FilmDTO result = filmService.replaceFilm(1, request);

        assertThat(result).isNotNull();
    }

    @Test
    void testPatchFilm() {
        Film film = new Film();
        film.setFilmId(1);

        when(filmRepository.findById(1)).thenReturn(Optional.of(film));
        when(filmRepository.save(film)).thenReturn(film);

        FilmDTO result = filmService.patchFilm(1, new com.film.dto.FilmPatchDTO());

        assertThat(result).isNotNull();
    }
}