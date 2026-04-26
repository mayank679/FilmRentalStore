package com.film.controller;

import com.film.dto.FilmCategoryDTO;
import com.film.dto.FilmCategoryResponseDTO;
import com.film.entity.Film;
import com.film.services.FilmCategoryService;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;

import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;
import java.util.Map;

import static org.mockito.Mockito.when;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.eq;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(FilmCategoryController.class)
class FilmCategoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private FilmCategoryService filmCategoryService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testGetAll() throws Exception {
        FilmCategoryResponseDTO res = new FilmCategoryResponseDTO();
        res.setFilmId(1);
        res.setCategoryId(1);

        when(filmCategoryService.getAll()).thenReturn(List.of(res));

        mockMvc.perform(get("/api/film-categories"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].filmId").value(1));
    }

    @Test
    void testGetByFilm() throws Exception {
        FilmCategoryResponseDTO res = new FilmCategoryResponseDTO();
        res.setFilmId(1);

        when(filmCategoryService.getByFilm(1)).thenReturn(List.of(res));

        mockMvc.perform(get("/api/film-categories")
                        .param("filmId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].filmId").value(1));
    }

    @Test
    void testGetByCategory() throws Exception {
        FilmCategoryResponseDTO res = new FilmCategoryResponseDTO();
        res.setCategoryId(1);

        when(filmCategoryService.getByCategory(1)).thenReturn(List.of(res));

        mockMvc.perform(get("/api/film-categories")
                        .param("categoryId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].categoryId").value(1));
    }

    @Test
    void testGetByCategoryName() throws Exception {
        FilmCategoryResponseDTO res = new FilmCategoryResponseDTO();
        res.setCategoryId(1);

        when(filmCategoryService.getByCategoryName("Action")).thenReturn(List.of(res));

        mockMvc.perform(get("/api/film-categories")
                        .param("categoryName", "Action"))
                .andExpect(status().isOk());
    }

    @Test
    void testGetById() throws Exception {
        FilmCategoryResponseDTO res = new FilmCategoryResponseDTO();
        res.setFilmId(1);
        res.setCategoryId(1);

        when(filmCategoryService.getById(1, 1)).thenReturn(res);

        mockMvc.perform(get("/api/film-categories/1/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.filmId").value(1));
    }

    @Test
    void testGetFilmsByCategory() throws Exception {
        FilmCategoryResponseDTO res = new FilmCategoryResponseDTO();
        res.setCategoryId(1);

        when(filmCategoryService.getByCategory(1)).thenReturn(List.of(res));

        mockMvc.perform(get("/api/film-categories/category/1/films"))
                .andExpect(status().isOk());
    }

    @Test
    void testGetCategoriesByFilm() throws Exception {
        FilmCategoryResponseDTO res = new FilmCategoryResponseDTO();
        res.setFilmId(1);

        when(filmCategoryService.getByFilm(1)).thenReturn(List.of(res));

        mockMvc.perform(get("/api/film-categories/film/1/categories"))
                .andExpect(status().isOk());
    }

    @Test
    void testGetByRating() throws Exception {
        FilmCategoryResponseDTO res = new FilmCategoryResponseDTO();
        res.setFilmId(1);

        when(filmCategoryService.getByCategoryAndRating(eq(1), any()))
                .thenReturn(List.of(res));

        mockMvc.perform(get("/api/film-categories/category/1/rating/1"))
                .andExpect(status().is5xxServerError());
    }

    @Test
    void testCountFilmsInCategory() throws Exception {
        when(filmCategoryService.countFilmsInCategory(1)).thenReturn(5L);

        mockMvc.perform(get("/api/film-categories/category/1/count"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.filmCount").value(5));
    }

    @Test
    void testCountCategoriesForFilm() throws Exception {
        when(filmCategoryService.countCategoriesForFilm(1)).thenReturn(3L);

        mockMvc.perform(get("/api/film-categories/film/1/count"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.categoryCount").value(3));
    }

    @Test
    void testCreate() throws Exception {
        FilmCategoryDTO request = new FilmCategoryDTO();
        request.setFilmId(1);
        request.setCategoryId(1);

        FilmCategoryResponseDTO res = new FilmCategoryResponseDTO();
        res.setFilmId(1);
        res.setCategoryId(1);

        when(filmCategoryService.create(any(FilmCategoryDTO.class)))
                .thenReturn(res);

        mockMvc.perform(post("/api/film-categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.filmId").value(1));
    }

    @Test
    void testReplace() throws Exception {
        FilmCategoryDTO request = new FilmCategoryDTO();
        request.setFilmId(1);
        request.setCategoryId(1);

        FilmCategoryResponseDTO res = new FilmCategoryResponseDTO();
        res.setFilmId(1);
        res.setCategoryId(1);

        when(filmCategoryService.replace(eq(1), eq(1), any(FilmCategoryDTO.class)))
                .thenReturn(res);

        mockMvc.perform(put("/api/film-categories/1/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.filmId").value(1));
    }
}