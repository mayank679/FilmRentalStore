package com.film.services;

import com.film.dto.LanguageDTO;
import com.film.dto.LanguageRequestDTO;
import com.film.dto.LanguageResponseDTO;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;

import java.util.List;

public interface LanguageService {
    Page<LanguageDTO> findAllPaginated(int page, int size);
    List<LanguageDTO> getAllLanguages();
    LanguageResponseDTO createLanguage(@Valid LanguageRequestDTO request);
    LanguageDTO getLanguageById(Integer id);
    LanguageResponseDTO updateLanguage(Integer id, @Valid LanguageRequestDTO request);
}
