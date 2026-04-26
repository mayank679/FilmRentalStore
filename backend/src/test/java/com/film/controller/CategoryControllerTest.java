package com.film.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.film.dto.CategoryRequestDTO;
import com.film.dto.CategoryResponseDTO;
import com.film.exception.CategoryNotFoundException;
import com.film.exception.EmptyResultException;
import com.film.services.CategoryService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CategoryController.class)
@DisplayName("CategoryController MockMvc Tests")
class CategoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CategoryService categoryService;

    @Autowired
    private ObjectMapper objectMapper;

    // ─── Helper builders ──────────────────────────────────────────────────────

    private CategoryResponseDTO buildResponse(int id, String name) {
        return CategoryResponseDTO.builder()
                .categoryId(id)
                .name(name)
                .build();
    }

    private CategoryRequestDTO buildRequest(String name) {
        CategoryRequestDTO req = new CategoryRequestDTO();
        req.setName(name);
        return req;
    }

    // ─── GET /categories ─────────────────────────────────────────────────────

    @Test
    @DisplayName("GET /categories - returns 200 with all categories")
    void getAllCategories_returns200() throws Exception {
        when(categoryService.getAllCategories())
                .thenReturn(List.of(
                        buildResponse(1, "Action"),
                        buildResponse(2, "Comedy")));

        mockMvc.perform(get("/categories"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].name").value("Action"));
    }

    @Test
    @DisplayName("GET /categories - returns 404 when no categories exist")
    void getAllCategories_empty() throws Exception {
        when(categoryService.getAllCategories())
                .thenThrow(new EmptyResultException("No categories found in the database."));

        mockMvc.perform(get("/categories"))
                .andExpect(status().isNotFound());
    }

    // ─── GET /categories/{id} ────────────────────────────────────────────────

    @Test
    @DisplayName("GET /categories/{id} - returns 200 when category found")
    void getCategoryById_found() throws Exception {
        when(categoryService.getCategoryById(1)).thenReturn(buildResponse(1, "Action"));

        mockMvc.perform(get("/categories/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.categoryId").value(1))
                .andExpect(jsonPath("$.name").value("Action"));
    }

    @Test
    @DisplayName("GET /categories/{id} - returns 404 when category not found")
    void getCategoryById_notFound() throws Exception {
        when(categoryService.getCategoryById(999))
                .thenThrow(new CategoryNotFoundException(999));

        mockMvc.perform(get("/categories/999"))
                .andExpect(status().isNotFound());
    }

    // ─── POST /categories ────────────────────────────────────────────────────

    @Test
    @DisplayName("POST /categories - returns 201 with created category")
    void createCategory_success() throws Exception {
        when(categoryService.createCategory(any(CategoryRequestDTO.class)))
                .thenReturn(buildResponse(5, "Horror"));

        mockMvc.perform(post("/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(buildRequest("Horror"))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.categoryId").value(5))
                .andExpect(jsonPath("$.name").value("Horror"));
    }

    @Test
    @DisplayName("POST /categories - returns 409 when category name already exists")
    void createCategory_duplicate() throws Exception {
        when(categoryService.createCategory(any(CategoryRequestDTO.class)))
                .thenThrow(new IllegalArgumentException("Category with name Action already exists"));

        mockMvc.perform(post("/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(buildRequest("Action"))))
                .andExpect(status().isConflict());
    }

    @Test
    @DisplayName("POST /categories - returns 400 when name is blank")
    void createCategory_validationFails() throws Exception {
        CategoryRequestDTO invalid = new CategoryRequestDTO();
        // name is null — violates @NotBlank

        mockMvc.perform(post("/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalid)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /categories - returns 400 when name exceeds 25 characters")
    void createCategory_nameTooLong() throws Exception {
        CategoryRequestDTO req = buildRequest("ThisNameIsWayTooLongForACategory"); // 32 chars

        mockMvc.perform(post("/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest());
    }

    // ─── PUT /categories/{id} ────────────────────────────────────────────────

    @Test
    @DisplayName("PUT /categories/{id} - returns 200 with updated category")
    void replaceCategory_success() throws Exception {
        when(categoryService.replaceCategory(eq(1), any(CategoryRequestDTO.class)))
                .thenReturn(buildResponse(1, "Thriller"));

        mockMvc.perform(put("/categories/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(buildRequest("Thriller"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.categoryId").value(1))
                .andExpect(jsonPath("$.name").value("Thriller"));
    }

    @Test
    @DisplayName("PUT /categories/{id} - returns 404 when category not found")
    void replaceCategory_notFound() throws Exception {
        when(categoryService.replaceCategory(eq(999), any(CategoryRequestDTO.class)))
                .thenThrow(new CategoryNotFoundException(999));

        mockMvc.perform(put("/categories/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(buildRequest("Thriller"))))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("PUT /categories/{id} - returns 400 when request body is invalid")
    void replaceCategory_validationFails() throws Exception {
        CategoryRequestDTO invalid = new CategoryRequestDTO();
        // name is null — violates @NotBlank

        mockMvc.perform(put("/categories/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalid)))
                .andExpect(status().isBadRequest());
    }
}