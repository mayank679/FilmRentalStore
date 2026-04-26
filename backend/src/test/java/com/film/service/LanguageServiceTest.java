package com.film.service;

import com.film.dto.LanguageRequestDTO;
import com.film.entity.Language;
import com.film.repository.LanguageRepository;
import com.film.services.LanguageServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class LanguageServiceImplTest {

    @Mock
    private LanguageRepository languageRepository;

    @InjectMocks
    private LanguageServiceImpl service;

    private Language getLanguage(Integer id, String name) {
        Language lang = new Language();
        lang.setLanguageId(id.byteValue());
        lang.setName(name);
        return lang;
    }

    @Test
    void testGetAllLanguages() {
        when(languageRepository.findAll())
                .thenReturn(List.of(getLanguage(1, "English")));

        var result = service.getAllLanguages();

        assertThat(result).isNotNull();
        assertThat(result).hasSize(1);
    }

    @Test
    void testCreateLanguageSuccess() {
        LanguageRequestDTO request = new LanguageRequestDTO();
        request.setName("Hindi");

        when(languageRepository.existsByName("Hindi"))
                .thenReturn(false);

        when(languageRepository.save(any()))
                .thenAnswer(i -> {
                    Language l = i.getArgument(0);
                    l.setLanguageId((byte) 1);
                    return l;
                });

        var result = service.createLanguage(request);

        assertThat(result.getName()).isEqualTo("Hindi");
        assertThat(result.getLanguageId()).isEqualTo((byte) 1);
    }

    @Test
    void testCreateLanguageDuplicate() {
        LanguageRequestDTO request = new LanguageRequestDTO();
        request.setName("English");

        when(languageRepository.existsByName("English"))
                .thenReturn(true);

        assertThatThrownBy(() -> service.createLanguage(request))
                .isInstanceOf(IllegalArgumentException.class);
    }
}