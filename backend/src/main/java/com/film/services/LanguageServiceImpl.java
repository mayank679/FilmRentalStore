package com.film.services;

import com.film.dto.LanguageDTO;
import com.film.dto.LanguageRequestDTO;
import com.film.dto.LanguageResponseDTO;
import com.film.entity.Language;
import com.film.exception.LanguageNotFoundException;
import com.film.repository.LanguageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class LanguageServiceImpl implements LanguageService {

    @Autowired
    private LanguageRepository languageRepository;

    private LanguageDTO toLanguageDTO(Language lang) {
        return new LanguageDTO(
                lang.getLanguageId() != null ? lang.getLanguageId().intValue() : null,
                lang.getName()
        );
    }

    @Override
    public Page<LanguageDTO> findAllPaginated(int page, int size) {
        return languageRepository.findAll(PageRequest.of(page, size))
                .map(this::toLanguageDTO);
    }

    @Override
    public List<LanguageDTO> getAllLanguages() {
        return languageRepository.findAll()
                .stream()
                .map(this::toLanguageDTO)
                .collect(Collectors.toList());
    }

    @Override
    public LanguageDTO getLanguageById(Integer id) {
        Language language = languageRepository.findById(id)
                .orElseThrow(() -> new LanguageNotFoundException(id));
        return toLanguageDTO(language);
    }

    @Override
    public LanguageResponseDTO createLanguage(LanguageRequestDTO request) {
        if (languageRepository.existsByName(request.getName())) {
            throw new IllegalArgumentException("Language already exists");
        }
        Language language = Language.builder()
                .name(request.getName())
                .lastUpdate(LocalDateTime.now())
                .build();
        languageRepository.save(language);
        return toDTO(language);
    }

    @Override
    public LanguageResponseDTO updateLanguage(Integer id, LanguageRequestDTO request) {
        Language language = languageRepository.findById(id)
                .orElseThrow(() -> new LanguageNotFoundException(id));
        language.setName(request.getName());
        language.setLastUpdate(LocalDateTime.now());
        languageRepository.save(language);
        return toDTO(language);
    }

    private LanguageResponseDTO toDTO(Language language) {
        return LanguageResponseDTO.builder()
                .languageId(language.getLanguageId())
                .name(language.getName())
                .build();
    }
}
