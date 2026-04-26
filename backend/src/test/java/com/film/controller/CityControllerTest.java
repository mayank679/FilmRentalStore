package com.film.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.film.dto.CityDto;
import com.film.exception.DuplicateResourceException;
import com.film.exception.ResourceNotFoundException;
import com.film.services.CityService;
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

@WebMvcTest(CityController.class)
@DisplayName("CityController MockMvc Tests")
class CityControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CityService cityService;

    @Autowired
    private ObjectMapper objectMapper;

    private CityDto.Response buildResponse(int cityId, String city, int countryId) {
        return CityDto.Response.builder()
                .cityId(cityId)
                .city(city)
                .countryId(countryId)
                .lastUpdate(LocalDateTime.now())
                .build();
    }

    // FIX: buildDetailedResponse returns CityDto.DetailedResponse for FK-based endpoints
    private CityDto.DetailedResponse buildDetailedResponse(int cityId, String city, int countryId, String countryName) {
        return CityDto.DetailedResponse.builder()
                .cityId(cityId)
                .city(city)
                .countryId(countryId)
                .countryName(countryName)
                .lastUpdate(LocalDateTime.now())
                .build();
    }

    // GET ALL
    @Test
    @DisplayName("GET /api/cities - returns 200 with paginated cities")
    void getAll_returns200() throws Exception {
        Page<CityDto.Response> page =
                new PageImpl<>(List.of(buildResponse(10, "Kolkata", 1)));
        when(cityService.findAllPaginated(0, 10)).thenReturn(page);

        mockMvc.perform(get("/api/cities").param("page", "0").param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].city").value("Kolkata"))
                .andExpect(jsonPath("$.content[0].cityId").value(10));
    }

    // GET BY ID
    @Test
    @DisplayName("GET /api/cities/{id} - returns 200 when found")
    void getById_found() throws Exception {
        when(cityService.findById(10)).thenReturn(buildResponse(10, "Kolkata", 1));

        mockMvc.perform(get("/api/cities/10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.cityId").value(10))
                .andExpect(jsonPath("$.city").value("Kolkata"));
    }

    @Test
    @DisplayName("GET /api/cities/{id} - returns 404 when not found")
    void getById_notFound() throws Exception {
        when(cityService.findById(99))
                .thenThrow(new ResourceNotFoundException("City", "id", 99));

        mockMvc.perform(get("/api/cities/99"))
                .andExpect(status().isNotFound());
    }

    // GET BY NAME
    @Test
    @DisplayName("GET /api/cities/name/{name} - returns 200 when cities match")
    void getByName_found() throws Exception {
        when(cityService.getCitiesByName("kol"))
                .thenReturn(List.of(buildResponse(10, "Kolkata", 1)));

        mockMvc.perform(get("/api/cities/name/kol"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].city").value("Kolkata"));
    }

    @Test
    @DisplayName("GET /api/cities/name/{name} - returns 404 when no cities match")
    void getByName_notFound() throws Exception {
        when(cityService.getCitiesByName("xyz"))
                .thenThrow(new ResourceNotFoundException("City", "name", "xyz"));

        mockMvc.perform(get("/api/cities/name/xyz"))
                .andExpect(status().isNotFound());
    }

    // GET BY COUNTRY ID
    // FIX: stub must return List<CityDto.DetailedResponse> to match service signature
    @Test
    @DisplayName("GET /api/cities/country/id/{countryId} - returns list of cities with countryName")
    void getByCountryId_found() throws Exception {
        when(cityService.findByCountryId(1))
                .thenReturn(List.of(buildDetailedResponse(10, "Kolkata", 1, "India")));

        mockMvc.perform(get("/api/cities/country/id/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].countryId").value(1))
                .andExpect(jsonPath("$[0].countryName").value("India"));
    }

    @Test
    @DisplayName("GET /api/cities/country/id/{countryId} - returns empty array when no cities")
    void getByCountryId_empty() throws Exception {
        // FIX: return empty List<CityDto.DetailedResponse>, not List<CityDto.Response>
        when(cityService.findByCountryId(99)).thenReturn(List.of());

        mockMvc.perform(get("/api/cities/country/id/99"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());
    }

    // POST
    @Test
    @DisplayName("POST /api/cities - returns 201 and created city")
    void create_success() throws Exception {
        CityDto.Request req = CityDto.Request.builder()
                .city("Mumbai")
                .countryId(1)
                .build();

        when(cityService.create(any(CityDto.Request.class)))
                .thenReturn(buildResponse(11, "Mumbai", 1));

        mockMvc.perform(post("/api/cities")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.city").value("Mumbai"))
                .andExpect(jsonPath("$.cityId").value(11));
    }

    @Test
    @DisplayName("POST /api/cities - returns 409 when city already exists in country")
    void create_duplicate() throws Exception {
        CityDto.Request req = CityDto.Request.builder()
                .city("Kolkata")
                .countryId(1)
                .build();

        when(cityService.create(any(CityDto.Request.class)))
                .thenThrow(new DuplicateResourceException("City 'Kolkata' already exists in this country"));

        mockMvc.perform(post("/api/cities")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isConflict());
    }

    @Test
    @DisplayName("POST /api/cities - returns 400 when city name is blank")
    void create_validationFails_blankCity() throws Exception {
        CityDto.Request req = CityDto.Request.builder()
                .city("")
                .countryId(1)
                .build();

        mockMvc.perform(post("/api/cities")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /api/cities - returns 400 when countryId is null")
    void create_validationFails_nullCountryId() throws Exception {
        CityDto.Request req = CityDto.Request.builder()
                .city("SomeCity")
                .countryId(null)
                .build();

        mockMvc.perform(post("/api/cities")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest());
    }

    // PUT
    @Test
    @DisplayName("PUT /api/cities/{id} - returns 200 and updated city")
    void update_success() throws Exception {
        CityDto.Request req = CityDto.Request.builder()
                .city("Kolkata Updated")
                .countryId(1)
                .build();

        when(cityService.update(eq(10), any(CityDto.Request.class)))
                .thenReturn(buildResponse(10, "Kolkata Updated", 1));

        mockMvc.perform(put("/api/cities/10")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.city").value("Kolkata Updated"));
    }

    @Test
    @DisplayName("PUT /api/cities/{id} - returns 404 when city not found")
    void update_notFound() throws Exception {
        CityDto.Request req = CityDto.Request.builder()
                .city("SomeCity")
                .countryId(1)
                .build();

        when(cityService.update(eq(99), any(CityDto.Request.class)))
                .thenThrow(new ResourceNotFoundException("City", "id", 99));

        mockMvc.perform(put("/api/cities/99")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isNotFound());
    }
}