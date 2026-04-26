package com.film.service;

import com.film.dto.FilmTextDTO;
import com.film.entity.Film;
import com.film.entity.FilmText;
import com.film.exception.DuplicateResourceException;
import com.film.exception.ResourceNotFoundException;
import com.film.repository.FilmRepository;
import com.film.repository.FilmTextRepository;
import com.film.services.FilmTextService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class FilmTextServiceTest {

    @Mock
    private FilmTextRepository filmTextRepository;

    @Mock
    private FilmRepository filmRepository;

    @InjectMocks
    private FilmTextService service;

    private Film getFilm(Integer id) {
        Film film = new Film();
        film.setFilmId(id);
        film.setTitle("Test Film");
        film.setReleaseYear((int) 20);
        film.setRentalDuration((short) 5);
        film.setLength((int) 100);
        film.setRentalRate(BigDecimal.valueOf(2.99));
        return film;
    }

    private FilmText getFilmText(Integer id) {
        FilmText ft = new FilmText();
        ft.setFilmId(id);
        ft.setTitle("Sample Title");
        ft.setDescription("Sample Desc");
        ft.setFilm(getFilm(id));
        return ft;
    }

    @Test
    void testGetAll() {
        when(filmTextRepository.findAll())
                .thenReturn(List.of(getFilmText(1)));

        var result = service.getAll();

        assertThat(result).hasSize(1);
    }

    @Test
    void testGetByFilmIdWithDetailsSuccess() {
        when(filmTextRepository.findByFilmIdWithDetails(1))
                .thenReturn(Optional.of(getFilmText(1)));

        var result = service.getByFilmIdWithDetails(1);

        assertThat(result.getFilmId()).isEqualTo(1);
    }

    @Test
    void testGetByFilmIdWithDetailsNotFound() {
        when(filmTextRepository.findByFilmIdWithDetails(1))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.getByFilmIdWithDetails(1))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void testGetByTitle() {
        when(filmTextRepository.findByTitleContainingIgnoreCase("test"))
                .thenReturn(List.of(getFilmText(1)));

        var result = service.getByTitle("test");

        assertThat(result).hasSize(1);
    }

    @Test
    void testGetByDescription() {
        when(filmTextRepository.findByDescriptionContainingIgnoreCase("desc"))
                .thenReturn(List.of(getFilmText(1)));

        var result = service.getByDescription("desc");

        assertThat(result).hasSize(1);
    }

    @Test
    void testGetByKeyword() {
        when(filmTextRepository.searchByKeyword("key"))
                .thenReturn(List.of(getFilmText(1)));

        var result = service.getByKeyword("key");

        assertThat(result).hasSize(1);
    }

    @Test
    void testCreateSuccess() {
        FilmTextDTO dto = new FilmTextDTO(1, "Title", "Desc");

        when(filmRepository.findById(1))
                .thenReturn(Optional.of(getFilm(1)));

        when(filmTextRepository.existsByFilmId(1))
                .thenReturn(false);

        when(filmTextRepository.save(any()))
                .thenAnswer(i -> i.getArgument(0));

        var result = service.create(dto);

        assertThat(result.getFilmId()).isEqualTo(1);
    }

    @Test
    void testCreateDuplicate() {
        FilmTextDTO dto = new FilmTextDTO(1, "Title", "Desc");

        when(filmRepository.findById(1))
                .thenReturn(Optional.of(getFilm(1)));

        when(filmTextRepository.existsByFilmId(1))
                .thenReturn(true);

        assertThatThrownBy(() -> service.create(dto))
                .isInstanceOf(DuplicateResourceException.class);
    }

    @Test
    void testCreateFilmNotFound() {
        FilmTextDTO dto = new FilmTextDTO(1, "Title", "Desc");

        when(filmRepository.findById(1))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.create(dto))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void testReplaceSuccess() {
        FilmText existing = getFilmText(1);

        FilmTextDTO dto = new FilmTextDTO(1, "New Title", "New Desc");

        when(filmTextRepository.findById(1))
                .thenReturn(Optional.of(existing));

        when(filmTextRepository.save(any()))
                .thenAnswer(i -> i.getArgument(0));

        var result = service.replace(1, dto);

        assertThat(result.getTitle()).isEqualTo("New Title");
    }

    @Test
    void testReplaceNotFound() {
        FilmTextDTO dto = new FilmTextDTO(1, "New Title", "New Desc");

        when(filmTextRepository.findById(1))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.replace(1, dto))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}