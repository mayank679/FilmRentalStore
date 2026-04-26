package com.film.controller;

import com.film.dto.FilmActorDTO;
import com.film.dto.FilmActorResponseDTO;
import com.film.services.FilmActorService;

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

@WebMvcTest(FilmActorController.class)
class FilmActorControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private FilmActorService filmActorService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testGetAll() throws Exception {
        FilmActorResponseDTO response = new FilmActorResponseDTO();
        response.setActorId(1);
        response.setFilmId(1);

        when(filmActorService.getAll()).thenReturn(List.of(response));

        mockMvc.perform(get("/api/filmactor"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].actorId").value(1));
    }

    @Test
    void testGetByFilm() throws Exception {
        FilmActorResponseDTO response = new FilmActorResponseDTO();
        response.setActorId(1);
        response.setFilmId(1);

        when(filmActorService.getByFilm(1)).thenReturn(List.of(response));

        mockMvc.perform(get("/api/filmactor")
                        .param("filmId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].filmId").value(1));
    }

    @Test
    void testGetByActor() throws Exception {
        FilmActorResponseDTO response = new FilmActorResponseDTO();
        response.setActorId(1);
        response.setFilmId(1);

        when(filmActorService.getByActor(1)).thenReturn(List.of(response));

        mockMvc.perform(get("/api/filmactor")
                        .param("actorId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].actorId").value(1));
    }

    @Test
    void testGetById() throws Exception {
        FilmActorResponseDTO response = new FilmActorResponseDTO();
        response.setActorId(1);
        response.setFilmId(1);

        when(filmActorService.getById(1, 1)).thenReturn(response);

        mockMvc.perform(get("/api/filmactor/1/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.actorId").value(1))
                .andExpect(jsonPath("$.filmId").value(1));
    }

    @Test
    void testCreate() throws Exception {
        FilmActorDTO request = new FilmActorDTO();
        request.setActorId(1);
        request.setFilmId(1);

        FilmActorResponseDTO response = new FilmActorResponseDTO();
        response.setActorId(1);
        response.setFilmId(1);

        when(filmActorService.create(any(FilmActorDTO.class)))
                .thenReturn(response);

        mockMvc.perform(post("/api/filmactor")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.actorId").value(1))
                .andExpect(jsonPath("$.filmId").value(1));
    }

    @Test
    void testReplace() throws Exception {
        FilmActorDTO request = new FilmActorDTO();
        request.setActorId(1);
        request.setFilmId(1);

        FilmActorResponseDTO response = new FilmActorResponseDTO();
        response.setActorId(1);
        response.setFilmId(1);

        when(filmActorService.replace(eq(1), eq(1), any(FilmActorDTO.class)))
                .thenReturn(response);

        mockMvc.perform(put("/api/filmactor/1/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.actorId").value(1))
                .andExpect(jsonPath("$.filmId").value(1));
    }
}