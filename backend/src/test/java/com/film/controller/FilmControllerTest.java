package com.film.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.film.dto.FilmDTO;
import com.film.dto.FilmPatchDTO;
import com.film.dto.FilmRequestDTO;
import com.film.exception.CategoryNotFoundException;
import com.film.exception.EmptyResultException;
import com.film.exception.FilmNotFoundException;
import com.film.exception.LanguageNotFoundException;
import com.film.services.FilmService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(FilmController.class)
@DisplayName("FilmController MockMvc Tests")
class FilmControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private FilmService filmService;

    @Autowired
    private ObjectMapper objectMapper;

    // ─── Helper builders ──────────────────────────────────────────────────────

    private FilmDTO buildFilmDTO(int id) {
        return FilmDTO.builder()
                .filmId(id)
                .title("ACADEMY DINOSAUR")
                .description("A Epic Drama of a Feminist And a Mad Scientist")
                .releaseYear(2006)
                .languageName("English")
                .rentalDuration((short) 6)
                .rentalRate(new BigDecimal("0.99"))
                .length(86)
                .replacementCost(new BigDecimal("20.99"))
                .rating("PG")
                .specialFeatures("Deleted Scenes,Behind the Scenes")
                .categoryNames(List.of("Documentary"))
                .build();
    }

    private FilmRequestDTO buildFilmRequest() {
        FilmRequestDTO req = new FilmRequestDTO();
        req.setTitle("ACADEMY DINOSAUR");
        req.setDescription("A Epic Drama");
        req.setReleaseYear(2006);
        req.setLanguageId(1);
        req.setRentalDuration((short) 6);
        req.setRentalRate(new BigDecimal("0.99"));
        req.setLength(86);
        req.setReplacementCost(new BigDecimal("20.99"));
        req.setRating("PG");
        req.setCategoryIds(List.of(1));
        return req;
    }

    // ─── GET /films/first10 ───────────────────────────────────────────────────

    @Test
    @DisplayName("GET /films/first10 - returns 200 with first 10 films")
    void getFirst10Films_returns200() throws Exception {
        when(filmService.getFirst10Films()).thenReturn(List.of(buildFilmDTO(1)));

        mockMvc.perform(get("/films/first10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].filmId").value(1))
                .andExpect(jsonPath("$[0].title").value("ACADEMY DINOSAUR"));
    }

    // ─── GET /films ───────────────────────────────────────────────────────────

    @Test
    @DisplayName("GET /films - returns 200 with all films")
    void getAllFilms_returns200() throws Exception {
        when(filmService.getAllFilms()).thenReturn(List.of(buildFilmDTO(1), buildFilmDTO(2)));

        mockMvc.perform(get("/films"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    // ─── GET /films/{id} ──────────────────────────────────────────────────────

    @Test
    @DisplayName("GET /films/{id} - returns 200 when film found")
    void getFilmById_found() throws Exception {
        when(filmService.getFilmById(1)).thenReturn(buildFilmDTO(1));

        mockMvc.perform(get("/films/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.filmId").value(1))
                .andExpect(jsonPath("$.rating").value("PG"));
    }

    @Test
    @DisplayName("GET /films/{id} - returns 404 when film not found")
    void getFilmById_notFound() throws Exception {
        when(filmService.getFilmById(999)).thenThrow(new FilmNotFoundException(999));

        mockMvc.perform(get("/films/999"))
                .andExpect(status().isNotFound());
    }

    // ─── GET /films/by-language-name ─────────────────────────────────────────

    @Test
    @DisplayName("GET /films/by-language-name - returns 200 with matching films")
    void getFilmsByLanguageName_found() throws Exception {
        when(filmService.getFilmsByLanguageName("English")).thenReturn(List.of(buildFilmDTO(1)));

        mockMvc.perform(get("/films/by-language-name").param("name", "English"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].languageName").value("English"));
    }

    @Test
    @DisplayName("GET /films/by-language-name - returns 404 when no films found")
    void getFilmsByLanguageName_notFound() throws Exception {
        when(filmService.getFilmsByLanguageName("Klingon"))
                .thenThrow(new EmptyResultException("language name", "Klingon"));

        mockMvc.perform(get("/films/by-language-name").param("name", "Klingon"))
                .andExpect(status().isNotFound());
    }

    // ─── GET /films/by-rating ─────────────────────────────────────────────────

    @Test
    @DisplayName("GET /films/by-rating - returns 200 with matching films")
    void getFilmsByRating_found() throws Exception {
        when(filmService.getFilmsByRating("PG")).thenReturn(List.of(buildFilmDTO(1)));

        mockMvc.perform(get("/films/by-rating").param("rating", "PG"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].rating").value("PG"));
    }

    // ─── GET /films/by-title ──────────────────────────────────────────────────

    @Test
    @DisplayName("GET /films/by-title - returns 200 when title matches")
    void getFilmsByTitle_found() throws Exception {
        when(filmService.getFilmByTitle("ACADEMY DINOSAUR")).thenReturn(List.of(buildFilmDTO(1)));

        mockMvc.perform(get("/films/by-title").param("title", "ACADEMY DINOSAUR"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("ACADEMY DINOSAUR"));
    }

    // ─── GET /films/by-release-year ───────────────────────────────────────────

    @Test
    @DisplayName("GET /films/by-release-year - returns 200 with films from that year")
    void getFilmsByReleaseYear_found() throws Exception {
        when(filmService.getFilmByReleaseYear(2006)).thenReturn(List.of(buildFilmDTO(1)));

        mockMvc.perform(get("/films/by-release-year").param("releaseYear", "2006"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].releaseYear").value(2006));
    }

    // ─── GET /films/by-language-id ────────────────────────────────────────────

    @Test
    @DisplayName("GET /films/by-language-id - returns 200 with films for given language")
    void getFilmsByLanguageId_found() throws Exception {
        when(filmService.getFilmsByLanguageId(1)).thenReturn(List.of(buildFilmDTO(1)));

        mockMvc.perform(get("/films/by-language-id").param("languageId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].languageName").value("English"));
    }

    // ─── GET /films/by-rental-rate ────────────────────────────────────────────

    @Test
    @DisplayName("GET /films/by-rental-rate - returns 200 with matching films")
    void getFilmsByRentalRate_found() throws Exception {
        when(filmService.getFilmsByRentalRate(new BigDecimal("0.99")))
                .thenReturn(List.of(buildFilmDTO(1)));

        mockMvc.perform(get("/films/by-rental-rate").param("rentalRate", "0.99"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].rentalRate").value(0.99));
    }

    // ─── GET /films/{filmId}/categories ───────────────────────────────────────

    @Test
    @DisplayName("GET /films/{filmId}/categories - returns 200 with film and its categories")
    void getFilmWithCategories_found() throws Exception {
        when(filmService.getFilmWithCategories(1)).thenReturn(buildFilmDTO(1));

        mockMvc.perform(get("/films/1/categories"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.filmId").value(1))
                .andExpect(jsonPath("$.categoryNames[0]").value("Documentary"));
    }

    @Test
    @DisplayName("GET /films/{filmId}/categories - returns 404 when film has no categories")
    void getFilmWithCategories_empty() throws Exception {
        when(filmService.getFilmWithCategories(999))
                .thenThrow(new FilmNotFoundException(999));

        mockMvc.perform(get("/films/999/categories"))
                .andExpect(status().isNotFound());
    }

    // ─── GET /films/count-by-category ────────────────────────────────────────

    @Test
    @DisplayName("GET /films/count-by-category - returns 200 with category count data")
    void getFilmCountByCategory_returns200() throws Exception {
        when(filmService.getFilmCountByCategory()).thenReturn(List.of());

        mockMvc.perform(get("/films/count-by-category"))
                .andExpect(status().isOk());
    }

    // ─── GET /films/count-by-rating ───────────────────────────────────────────

    @Test
    @DisplayName("GET /films/count-by-rating - returns 200 with rating count data")
    void getCountByRating_returns200() throws Exception {
        when(filmService.getCountByRating()).thenReturn(List.of());

        mockMvc.perform(get("/films/count-by-rating"))
                .andExpect(status().isOk());
    }

    // ─── GET /films/count-by-release-year ────────────────────────────────────

    @Test
    @DisplayName("GET /films/count-by-release-year - returns 200 with release year counts")
    void getCountByReleaseYear_returns200() throws Exception {
        when(filmService.getCountByReleaseYear()).thenReturn(List.of());

        mockMvc.perform(get("/films/count-by-release-year"))
                .andExpect(status().isOk());
    }

    // ─── GET /films/avg-rental-rate ───────────────────────────────────────────

    @Test
    @DisplayName("GET /films/avg-rental-rate - returns 200 with average rental rate")
    void getAvgRentalRate_returns200() throws Exception {
        when(filmService.getAvgRentalRate()).thenReturn(new BigDecimal("2.98"));

        mockMvc.perform(get("/films/avg-rental-rate"))
                .andExpect(status().isOk());
    }

    // ─── GET /films/avg-length ────────────────────────────────────────────────

    @Test
    @DisplayName("GET /films/avg-length - returns 200 with average film length")
    void getAvgLength_returns200() throws Exception {
        when(filmService.getAvgLength()).thenReturn(115.27);

        mockMvc.perform(get("/films/avg-length"))
                .andExpect(status().isOk());
    }

    // ─── GET /films/total-replacement-cost ───────────────────────────────────

    @Test
    @DisplayName("GET /films/total-replacement-cost - returns 200 with total replacement cost")
    void getTotalReplacementCost_returns200() throws Exception {
        when(filmService.getTotalReplacementCost()).thenReturn(new BigDecimal("19984.00"));

        mockMvc.perform(get("/films/total-replacement-cost"))
                .andExpect(status().isOk());
    }

    // ─── GET /films/total-rental-duration ────────────────────────────────────

    @Test
    @DisplayName("GET /films/total-rental-duration - returns 200 with total rental duration")
    void getTotalRentalDuration_returns200() throws Exception {
        when(filmService.getTotalRentalDuration()).thenReturn(5000);

        mockMvc.perform(get("/films/total-rental-duration"))
                .andExpect(status().isOk());
    }

    // ─── GET /films/by-id-and-rating ─────────────────────────────────────────

    @Test
    @DisplayName("GET /films/by-id-and-rating - returns 200 when match found")
    void getByIdAndRating_found() throws Exception {
        when(filmService.getByIdAndRating(1, "PG")).thenReturn(List.of(buildFilmDTO(1)));

        mockMvc.perform(get("/films/by-id-and-rating")
                        .param("filmId", "1")
                        .param("rating", "PG"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].filmId").value(1));
    }

    @Test
    @DisplayName("GET /films/by-id-and-rating - returns 404 when no match")
    void getByIdAndRating_notFound() throws Exception {
        when(filmService.getByIdAndRating(1, "R"))
                .thenThrow(new FilmNotFoundException(1, "R"));

        mockMvc.perform(get("/films/by-id-and-rating")
                        .param("filmId", "1")
                        .param("rating", "R"))
                .andExpect(status().isNotFound());
    }

    // ─── GET /films/by-rating-and-rate ───────────────────────────────────────

    @Test
    @DisplayName("GET /films/by-rating-and-rate - returns 200 with matching films")
    void getByRatingAndRate_found() throws Exception {
        when(filmService.getByRatingAndRate("PG", new BigDecimal("0.99")))
                .thenReturn(List.of(buildFilmDTO(1)));

        mockMvc.perform(get("/films/by-rating-and-rate")
                        .param("rating", "PG")
                        .param("rentalRate", "0.99"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].rating").value("PG"));
    }

    // ─── GET /films/by-length-and-year ───────────────────────────────────────

    @Test
    @DisplayName("GET /films/by-length-and-year - returns 200 with matching films")
    void getByLengthAndYear_found() throws Exception {
        when(filmService.getByLengthAndYear(86, 2006)).thenReturn(List.of(buildFilmDTO(1)));

        mockMvc.perform(get("/films/by-length-and-year")
                        .param("length", "86")
                        .param("releaseYear", "2006"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].length").value(86));
    }

    // ─── GET /films/max-rental-rate ───────────────────────────────────────────

    @Test
    @DisplayName("GET /films/max-rental-rate - returns 200 with max rental rate")
    void getMaxRentalRate_returns200() throws Exception {
        when(filmService.getMaxRentalRate()).thenReturn(new BigDecimal("4.99"));

        mockMvc.perform(get("/films/max-rental-rate"))
                .andExpect(status().isOk());
    }

    // ─── GET /films/min-rental-rate ───────────────────────────────────────────

    @Test
    @DisplayName("GET /films/min-rental-rate - returns 200 with min rental rate")
    void getMinRentalRate_returns200() throws Exception {
        when(filmService.getMinRentalRate()).thenReturn(new BigDecimal("0.99"));

        mockMvc.perform(get("/films/min-rental-rate"))
                .andExpect(status().isOk());
    }

    // ─── GET /films/max-replacement-cost ─────────────────────────────────────

    @Test
    @DisplayName("GET /films/max-replacement-cost - returns 200 with max replacement cost")
    void getMaxReplacementCost_returns200() throws Exception {
        when(filmService.getMaxReplacementCost()).thenReturn(new BigDecimal("29.99"));

        mockMvc.perform(get("/films/max-replacement-cost"))
                .andExpect(status().isOk());
    }

    // ─── GET /films/min-replacement-cost ─────────────────────────────────────

    @Test
    @DisplayName("GET /films/min-replacement-cost - returns 200 with min replacement cost")
    void getMinReplacementCost_returns200() throws Exception {
        when(filmService.getMinReplacementCost()).thenReturn(new BigDecimal("9.99"));

        mockMvc.perform(get("/films/min-replacement-cost"))
                .andExpect(status().isOk());
    }

    // ─── GET /films/rental-rate-range ────────────────────────────────────────

    @Test
    @DisplayName("GET /films/rental-rate-range - returns 200 with films in range")
    void getFilmsByRentalRateRange_returns200() throws Exception {
        when(filmService.getFilmsByRentalRateRange(new BigDecimal("0.99"), new BigDecimal("2.99")))
                .thenReturn(List.of(buildFilmDTO(1)));

        mockMvc.perform(get("/films/rental-rate-range")
                        .param("min", "0.99")
                        .param("max", "2.99"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].rentalRate").value(0.99));
    }

    // ─── GET /films/replacement-cost-range ───────────────────────────────────

    @Test
    @DisplayName("GET /films/replacement-cost-range - returns 200 with films in range")
    void getFilmsByReplacementCostRange_returns200() throws Exception {
        when(filmService.getFilmsByReplacementCostRange(new BigDecimal("10.00"), new BigDecimal("25.00")))
                .thenReturn(List.of(buildFilmDTO(1)));

        mockMvc.perform(get("/films/replacement-cost-range")
                        .param("min", "10.00")
                        .param("max", "25.00"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].replacementCost").value(20.99));
    }

    // ─── POST /films ──────────────────────────────────────────────────────────

    @Test
    @DisplayName("POST /films - returns 201 with created film")
    void createFilm_success() throws Exception {
        when(filmService.createFilm(any(FilmRequestDTO.class))).thenReturn(buildFilmDTO(100));

        mockMvc.perform(post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(buildFilmRequest())))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.filmId").value(100))
                .andExpect(jsonPath("$.title").value("ACADEMY DINOSAUR"));
    }

    @Test
    @DisplayName("POST /films - returns 404 when language not found")
    void createFilm_languageNotFound() throws Exception {
        when(filmService.createFilm(any(FilmRequestDTO.class)))
                .thenThrow(new LanguageNotFoundException(99));

        mockMvc.perform(post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(buildFilmRequest())))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("POST /films - returns 404 when category not found")
    void createFilm_categoryNotFound() throws Exception {
        when(filmService.createFilm(any(FilmRequestDTO.class)))
                .thenThrow(new CategoryNotFoundException(99));

        mockMvc.perform(post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(buildFilmRequest())))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("POST /films - returns 400 when required fields are missing")
    void createFilm_validationFails() throws Exception {
        FilmRequestDTO invalid = new FilmRequestDTO(); // missing title, languageId, rentalDuration, etc.

        mockMvc.perform(post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalid)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /films - returns 400 when rating value is invalid")
    void createFilm_invalidRating() throws Exception {
        FilmRequestDTO req = buildFilmRequest();
        req.setRating("XX"); // not one of G|PG|PG-13|R|NC-17

        mockMvc.perform(post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest());
    }

    // ─── PUT /films/{id} ──────────────────────────────────────────────────────

    @Test
    @DisplayName("PUT /films/{id} - returns 200 with replaced film")
    void replaceFilm_success() throws Exception {
        when(filmService.replaceFilm(eq(1), any(FilmRequestDTO.class))).thenReturn(buildFilmDTO(1));

        mockMvc.perform(put("/films/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(buildFilmRequest())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.filmId").value(1));
    }

    @Test
    @DisplayName("PUT /films/{id} - returns 404 when film not found")
    void replaceFilm_notFound() throws Exception {
        when(filmService.replaceFilm(eq(999), any(FilmRequestDTO.class)))
                .thenThrow(new FilmNotFoundException(999));

        mockMvc.perform(put("/films/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(buildFilmRequest())))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("PUT /films/{id} - returns 404 when language not found")
    void replaceFilm_languageNotFound() throws Exception {
        when(filmService.replaceFilm(eq(1), any(FilmRequestDTO.class)))
                .thenThrow(new LanguageNotFoundException(99));

        mockMvc.perform(put("/films/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(buildFilmRequest())))
                .andExpect(status().isNotFound());
    }

    // ─── PATCH /films/{id} ────────────────────────────────────────────────────

    @Test
    @DisplayName("PATCH /films/{id} - returns 200 with partially updated film")
    void patchFilm_success() throws Exception {
        FilmPatchDTO patch = new FilmPatchDTO();
        patch.setTitle("UPDATED TITLE");
        patch.setRating("R");

        when(filmService.patchFilm(eq(1), any(FilmPatchDTO.class))).thenReturn(buildFilmDTO(1));

        mockMvc.perform(patch("/films/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(patch)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.filmId").value(1));
    }

    @Test
    @DisplayName("PATCH /films/{id} - returns 404 when film not found")
    void patchFilm_notFound() throws Exception {
        FilmPatchDTO patch = new FilmPatchDTO();
        patch.setTitle("NEW TITLE");

        when(filmService.patchFilm(eq(999), any(FilmPatchDTO.class)))
                .thenThrow(new FilmNotFoundException(999));

        mockMvc.perform(patch("/films/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(patch)))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("PATCH /films/{id} - returns 400 when patch has invalid rating")
    void patchFilm_invalidRating() throws Exception {
        FilmPatchDTO patch = new FilmPatchDTO();
        patch.setRating("INVALID");

        mockMvc.perform(patch("/films/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(patch)))
                .andExpect(status().isBadRequest());
    }
}