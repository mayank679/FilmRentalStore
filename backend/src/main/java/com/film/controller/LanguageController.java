package com.film.controller;

import com.film.dto.LanguageDTO;
import com.film.dto.LanguageRequestDTO;
import com.film.dto.LanguageResponseDTO;
import com.film.exception.LanguageNotFoundException;
import com.film.services.LanguageService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import org.springframework.data.domain.Page;
import java.util.List;

@RestController
@RequestMapping("/api/languages")
public class LanguageController {

    @Autowired
    private LanguageService languageService;

    // GET all languages (paginated)
    @GetMapping("/paged")
    public ResponseEntity<?> getAllLanguagesPaged(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(languageService.findAllPaginated(page, size));
    }

    @GetMapping
    public List<LanguageDTO> getAllLanguages() {
        return languageService.getAllLanguages();
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getLanguageById(@PathVariable Integer id) {
        try {
            return ResponseEntity.ok(languageService.getLanguageById(id));
        } catch (LanguageNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @PostMapping
    public ResponseEntity<?> createLanguage(@RequestBody @Valid LanguageRequestDTO request) {
        LanguageResponseDTO created = languageService.createLanguage(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateLanguage(@PathVariable Integer id,
                                             @RequestBody @Valid LanguageRequestDTO request) {
        try {
            return ResponseEntity.ok(languageService.updateLanguage(id, request));
        } catch (LanguageNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
}