package com.film.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.film.dto.LanguageDTO;
import com.film.dto.LanguageRequestDTO;
import com.film.dto.LanguageResponseDTO;
import com.film.exception.GlobalExceptionHandler;
import com.film.services.LanguageService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(LanguageController.class)
@Import(GlobalExceptionHandler.class)
@DisplayName("LanguageController MockMvc Tests")
class LanguageControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private LanguageService languageService;

    @Autowired
    private ObjectMapper objectMapper;

    // ─── Helper builders ──────────────────────────────────────────────────────

    private LanguageDTO buildLanguageDTO(int id, String name) {
        return new LanguageDTO(id, name);
    }

    private LanguageResponseDTO buildLanguageResponse(byte id, String name) {
        return LanguageResponseDTO.builder()
                .languageId(id)
                .name(name)
                .build();
    }

    private LanguageRequestDTO buildRequest(String name) {
        LanguageRequestDTO req = new LanguageRequestDTO();
        req.setName(name);
        return req;
    }

    // ─── GET /api/languages ───────────────────────────────────────────────────

    @Test
    @DisplayName("GET /api/languages - returns 200 with all languages")
    void getAllLanguages_returns200() throws Exception {
        when(languageService.getAllLanguages())
                .thenReturn(List.of(
                        buildLanguageDTO(1, "English"),
                        buildLanguageDTO(2, "Italian")));

        mockMvc.perform(get("/api/languages"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].name").value("English"));
    }

    @Test
    @DisplayName("GET /api/languages - returns 200 with empty list when no languages exist")
    void getAllLanguages_returnsEmptyList() throws Exception {
        when(languageService.getAllLanguages()).thenReturn(List.of());

        mockMvc.perform(get("/api/languages"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());
    }

    // ─── POST /api/languages ──────────────────────────────────────────────────

    @Test
    @DisplayName("POST /api/languages - returns 201 with created language")
    void createLanguage_success() throws Exception {
        when(languageService.createLanguage(any(LanguageRequestDTO.class)))
                .thenReturn(buildLanguageResponse((byte) 3, "French"));

        mockMvc.perform(post("/api/languages")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(buildRequest("French"))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.languageId").value(3))
                .andExpect(jsonPath("$.name").value("French"));
    }

    @Test
    @DisplayName("POST /api/languages - returns 400 when name is blank")
    void createLanguage_validationFails() throws Exception {
        LanguageRequestDTO invalid = new LanguageRequestDTO();
        // name is null — violates @NotBlank

        mockMvc.perform(post("/api/languages")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalid)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /api/languages - returns 400 when language already exists")
    void createLanguage_duplicate() throws Exception {
        when(languageService.createLanguage(any(LanguageRequestDTO.class)))
                .thenThrow(new IllegalArgumentException("Language already exists"));

        mockMvc.perform(post("/api/languages")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(buildRequest("English"))))
                .andExpect(status().isBadRequest());
    }
}