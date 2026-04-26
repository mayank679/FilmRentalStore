package com.film.controller;

import com.film.dto.FilmTextDTO;
import com.film.dto.FilmTextDetailResponseDTO;
import com.film.dto.FilmTextResponseDTO;
import com.film.services.FilmTextService;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;

import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.eq;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(FilmTextController.class)
class FilmTextControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private FilmTextService filmTextService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testGetAllFilmTexts() throws Exception {
        FilmTextResponseDTO res = new FilmTextResponseDTO();
        res.setFilmId(1);

        when(filmTextService.getAll()).thenReturn(List.of(res));

        mockMvc.perform(get("/api/filmtexts"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].filmId").value(1));
    }

    @Test
    void testGetByFilmIdWithDetails() throws Exception {
        FilmTextDetailResponseDTO res = new FilmTextDetailResponseDTO();
        res.setFilmId(1);

        when(filmTextService.getByFilmIdWithDetails(1)).thenReturn(res);

        mockMvc.perform(get("/api/filmtexts/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.filmId").value(1));
    }

    @Test
    void testGetByTitle() throws Exception {
        FilmTextResponseDTO res = new FilmTextResponseDTO();
        res.setFilmId(1);

        when(filmTextService.getByTitle("academy")).thenReturn(List.of(res));

        mockMvc.perform(get("/api/filmtexts/search/title")
                        .param("title", "academy"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].filmId").value(1));
    }

    @Test
    void testGetByDescription() throws Exception {
        FilmTextResponseDTO res = new FilmTextResponseDTO();
        res.setFilmId(1);

        when(filmTextService.getByDescription("epic")).thenReturn(List.of(res));

        mockMvc.perform(get("/api/filmtexts/search/description")
                        .param("description", "epic"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].filmId").value(1));
    }

    @Test
    void testGetByKeyword() throws Exception {
        FilmTextResponseDTO res = new FilmTextResponseDTO();
        res.setFilmId(1);

        when(filmTextService.getByKeyword("drama")).thenReturn(List.of(res));

        mockMvc.perform(get("/api/filmtexts/search/keyword")
                        .param("keyword", "drama"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].filmId").value(1));
    }

    @Test
    void testCreateFilmText() throws Exception {
        FilmTextDTO request = new FilmTextDTO();
        request.setFilmId(1);
        request.setTitle("Test Title");
        request.setDescription("Test Description");

        FilmTextResponseDTO res = new FilmTextResponseDTO();
        res.setFilmId(1);

        when(filmTextService.create(any(FilmTextDTO.class))).thenReturn(res);

        mockMvc.perform(post("/api/filmtexts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.filmId").value(1));
    }

    @Test
    void testReplaceFilmText() throws Exception {
        FilmTextDTO request = new FilmTextDTO();
        request.setFilmId(1);
        request.setTitle("Updated Title");
        request.setDescription("Updated Description");

        FilmTextResponseDTO res = new FilmTextResponseDTO();
        res.setFilmId(1);

        when(filmTextService.replace(eq(1), any(FilmTextDTO.class)))
                .thenReturn(res);

        mockMvc.perform(put("/api/filmtexts/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.filmId").value(1));
    }
}