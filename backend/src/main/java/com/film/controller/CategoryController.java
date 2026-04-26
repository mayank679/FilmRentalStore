package com.film.controller;

import com.film.dto.CategoryRequestDTO;
import com.film.dto.CategoryResponseDTO;
import com.film.entity.Category;
import com.film.exception.CategoryNotFoundException;
import com.film.exception.EmptyResultException;
import com.film.services.CategoryService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    private ResponseEntity<Map<String, Object>> error(HttpStatus status, String message) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", LocalDateTime.now().toString());
        body.put("status", status.value());
        body.put("error", status.getReasonPhrase());
        body.put("message", message);
        return ResponseEntity.status(status).body(body);
    }

    @GetMapping("/paged")
    public ResponseEntity<Page<CategoryResponseDTO>> getAllCategoriesPaged(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(categoryService.findAllPaginated(page, size));
    }

    @GetMapping
    public ResponseEntity<?> getAllCategories() {
        try {
            return ResponseEntity.ok(categoryService.getAllCategories());
        } catch (EmptyResultException e) {
            return error(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getCategoryById(@PathVariable Integer id) {
        try {
            return ResponseEntity.ok(categoryService.getCategoryById(id));
        } catch (CategoryNotFoundException e) {
            return error(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @PostMapping
    public ResponseEntity<?> createCategory(@Valid @RequestBody CategoryRequestDTO request) {
        try {
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(categoryService.createCategory(request));
        } catch (IllegalArgumentException e) {
            return error(HttpStatus.CONFLICT, e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> replaceCategory(@PathVariable Integer id,
                                              @Valid @RequestBody CategoryRequestDTO request) {
        try {
            return ResponseEntity.ok(categoryService.replaceCategory(id, request));
        } catch (CategoryNotFoundException e) {
            return error(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }
}