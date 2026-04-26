package com.film.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.film.dto.CountryDto;
import com.film.exception.DuplicateResourceException;
import com.film.exception.ResourceNotFoundException;
import com.film.services.CountryService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.*;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CountryController.class)
@DisplayName("CountryController MockMvc Tests")
class CountryControllerTest {
 
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CountryService countryService;

    @Autowired
    private ObjectMapper objectMapper;

    private CountryDto.Response buildResponse(int id, String name) {
        CountryDto.Response r = new CountryDto.Response();
        r.setCountryId(id);
        r.setCountry(name);
        r.setLastUpdate(LocalDateTime.now());
        return r;
    }

    // GET ALL
    @Test
    @DisplayName("GET /api/countries - returns 200 with page of countries")
    void getAll_returns200() throws Exception {
        Page<CountryDto.Response> page =
                new PageImpl<>(List.of(buildResponse(1, "India")));
        when(countryService.findAllPaginated(0, 10)).thenReturn(page);

        mockMvc.perform(get("/api/countries")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].country").value("India"))
                .andExpect(jsonPath("$.content[0].countryId").value(1));
    }

    // GET BY ID
    @Test
    @DisplayName("GET /api/countries/{id} - returns 200 when found")
    void getById_found() throws Exception {
        when(countryService.findById(1)).thenReturn(buildResponse(1, "India"));

        mockMvc.perform(get("/api/countries/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.countryId").value(1))
                .andExpect(jsonPath("$.country").value("India"));
    }

    @Test
    @DisplayName("GET /api/countries/{id} - returns 404 when not found")
    void getById_notFound() throws Exception {
        when(countryService.findById(99))
                .thenThrow(new ResourceNotFoundException("Country", "id", 99));

        mockMvc.perform(get("/api/countries/99"))
                .andExpect(status().isNotFound());
    }

    // GET BY NAME
    @Test
    @DisplayName("GET /api/countries/name/{name} - returns 200 with matching countries")
    void getByName_found() throws Exception {
        when(countryService.getCountriesByName("ind"))
                .thenReturn(List.of(buildResponse(1, "India")));

        mockMvc.perform(get("/api/countries/name/ind"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].country").value("India"));
    }

    @Test
    @DisplayName("GET /api/countries/name/{name} - returns 404 when no match")
    void getByName_notFound() throws Exception {
        when(countryService.getCountriesByName("xyz"))
                .thenThrow(new ResourceNotFoundException("Country", "name", "xyz"));

        mockMvc.perform(get("/api/countries/name/xyz"))
                .andExpect(status().isNotFound());
    }

    // POST
    @Test
    @DisplayName("POST /api/countries - returns 200 and created country")
    void create_success() throws Exception {
        CountryDto.Request req = new CountryDto.Request();
        req.setCountry("Germany");

        when(countryService.create(any(CountryDto.Request.class)))
                .thenReturn(buildResponse(2, "Germany"));

        mockMvc.perform(post("/api/countries")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.country").value("Germany"))
                .andExpect(jsonPath("$.countryId").value(2));
    }

    @Test
    @DisplayName("POST /api/countries - returns 409 when name is duplicate")
    void create_duplicate() throws Exception {
        CountryDto.Request req = new CountryDto.Request();
        req.setCountry("India");

        when(countryService.create(any(CountryDto.Request.class)))
                .thenThrow(new DuplicateResourceException("Country 'India' already exists"));

        mockMvc.perform(post("/api/countries")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isConflict());
    }

    @Test
    @DisplayName("POST /api/countries - returns 400 when country name is blank")
    void create_validationFails() throws Exception {
        CountryDto.Request req = new CountryDto.Request();
        req.setCountry("");  // blank — fails @NotBlank

        mockMvc.perform(post("/api/countries")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest());
    }

    // PUT
    @Test
    @DisplayName("PUT /api/countries/{id} - returns 200 and updated country")
    void update_success() throws Exception {
        CountryDto.Request req = new CountryDto.Request();
        req.setCountry("India Updated");

        when(countryService.update(eq(1), any(CountryDto.Request.class)))
                .thenReturn(buildResponse(1, "India Updated"));

        mockMvc.perform(put("/api/countries/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.country").value("India Updated"));
    }

    @Test
    @DisplayName("PUT /api/countries/{id} - returns 404 when country not found")
    void update_notFound() throws Exception {
        CountryDto.Request req = new CountryDto.Request();
        req.setCountry("Something");

        when(countryService.update(eq(99), any(CountryDto.Request.class)))
                .thenThrow(new ResourceNotFoundException("Country", "id", 99));

        mockMvc.perform(put("/api/countries/99")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isNotFound());
    }
}
