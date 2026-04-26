package com.film.service;

import com.film.services.FilmCategoryService;
import com.film.dto.FilmCategoryDTO;
import com.film.entity.*;
import com.film.exception.DuplicateResourceException;
import com.film.exception.ResourceNotFoundException;
import com.film.repository.CategoryRepository;
import com.film.repository.FilmCategoryRepository;
import com.film.repository.FilmRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class FilmCategoryServiceTest {

    @Mock
    private FilmCategoryRepository filmCategoryRepository;

    @Mock
    private FilmRepository filmRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private FilmCategoryService service;

    private Film getFilm(Integer id) {
        Film film = new Film();
        film.setFilmId(id);
        film.setTitle("Test Film");
        film.setReleaseYear(2020);
        return film;
    }

    private Category getCategory(Integer id) {
        Category category = new Category();
        category.setCategoryId(id);
        category.setName("Action");
        return category;
    }

    private FilmCategory getFilmCategory(Integer filmId, Integer categoryId) {
        FilmCategory fc = new FilmCategory();
        fc.setId(new FilmCategoryId(filmId, categoryId));
        fc.setFilm(getFilm(filmId));
        fc.setCategory(getCategory(categoryId));
        return fc;
    }

    @Test
    void testCreateSuccess() {
        FilmCategoryDTO dto = new FilmCategoryDTO(1, 2);

        when(filmCategoryRepository.existsById(any())).thenReturn(false);
        when(filmRepository.findById(1)).thenReturn(Optional.of(getFilm(1)));
        when(categoryRepository.findById(2)).thenReturn(Optional.of(getCategory(2)));
        when(filmCategoryRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        var result = service.create(dto);

        assertThat(result).isNotNull();
        assertThat(result.getFilmId()).isEqualTo(1);
        assertThat(result.getCategoryId()).isEqualTo(2);
    }

    @Test
    void testCreateDuplicate() {
        FilmCategoryDTO dto = new FilmCategoryDTO(1, 2);

        when(filmCategoryRepository.existsById(any())).thenReturn(true);

        assertThatThrownBy(() -> service.create(dto))
                .isInstanceOf(DuplicateResourceException.class);
    }

    @Test
    void testGetByIdSuccess() {
        FilmCategory fc = getFilmCategory(1, 2);

        when(filmCategoryRepository.findById(any()))
                .thenReturn(Optional.of(fc));

        var result = service.getById(1, 2);

        assertThat(result.getFilmId()).isEqualTo(1);
    }

    @Test
    void testGetByIdNotFound() {
        when(filmCategoryRepository.findById(any()))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.getById(1, 2))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void testGetAll() {
        when(filmCategoryRepository.findAll())
                .thenReturn(List.of(getFilmCategory(1, 2)));

        var result = service.getAll();

        assertThat(result).hasSize(1);
    }

    @Test
    void testCountFilmsInCategory() {
        when(categoryRepository.existsById(2)).thenReturn(true);
        when(filmCategoryRepository.countByCategory_CategoryId(2))
                .thenReturn(5L);

        long count = service.countFilmsInCategory(2);

        assertThat(count).isEqualTo(5);
    }

    @Test
    void testCountFilmsInCategory_NotFound() {
        when(categoryRepository.existsById(2)).thenReturn(false);

        assertThatThrownBy(() -> service.countFilmsInCategory(2))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void testCountCategoriesForFilm() {
        when(filmRepository.existsById(1)).thenReturn(true);
        when(filmCategoryRepository.countByFilm_FilmId(1))
                .thenReturn(3L);

        long count = service.countCategoriesForFilm(1);

        assertThat(count).isEqualTo(3);
    }

    @Test
    void testReplaceNotFound() {
        when(filmCategoryRepository.findById(any()))
                .thenReturn(Optional.empty());

        FilmCategoryDTO dto = new FilmCategoryDTO(2, 3);

        assertThatThrownBy(() -> service.replace(1, 2, dto))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void testReplaceSuccess() {
        FilmCategory existing = getFilmCategory(1, 2);
        FilmCategoryDTO dto = new FilmCategoryDTO(3, 4);

        when(filmCategoryRepository.findById(any()))
                .thenReturn(Optional.of(existing));

        when(filmCategoryRepository.existsById(new FilmCategoryId(3, 4)))
                .thenReturn(false);

        when(filmRepository.findById(3)).thenReturn(Optional.of(getFilm(3)));
        when(categoryRepository.findById(4)).thenReturn(Optional.of(getCategory(4)));

        when(filmCategoryRepository.save(any()))
                .thenAnswer(i -> i.getArgument(0));

        var result = service.replace(1, 2, dto);

        assertThat(result.getFilmId()).isEqualTo(3);
        assertThat(result.getCategoryId()).isEqualTo(4);
    }

    @Test
    void testReplaceDuplicate() {
        FilmCategory existing = getFilmCategory(1, 2);
        FilmCategoryDTO dto = new FilmCategoryDTO(3, 4);

        when(filmCategoryRepository.findById(any()))
                .thenReturn(Optional.of(existing));

        when(filmCategoryRepository.existsById(new FilmCategoryId(3, 4)))
                .thenReturn(true);

        assertThatThrownBy(() -> service.replace(1, 2, dto))
                .isInstanceOf(DuplicateResourceException.class);
    }
}