package com.film.service;

import com.film.dto.CountryDto;
import com.film.entity.Country;
import com.film.exception.DuplicateResourceException;
import com.film.exception.ResourceNotFoundException;
import com.film.repository.CountryRepository;
import com.film.services.CountryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("CountryService Unit Tests")
class CountryServiceTest {

    @Mock
    private CountryRepository countryRepository;

    @InjectMocks
    private CountryService countryService;

    private Country sampleCountry;

    @BeforeEach
    void setUp() {
        sampleCountry = new Country();
        sampleCountry.setCountryId(1);
        sampleCountry.setCountry("India");
        sampleCountry.setLastUpdate(LocalDateTime.now());
    }

    // GET ALL
    @Test
    @DisplayName("findAllPaginated - returns mapped page of responses")
    void findAllPaginated_returnsPageOfResponses() {
        Page<Country> page = new PageImpl<>(List.of(sampleCountry));
        when(countryRepository.findAll(any(PageRequest.class))).thenReturn(page);

        Page<CountryDto.Response> result = countryService.findAllPaginated(0, 10);

        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getCountry()).isEqualTo("India");
        verify(countryRepository).findAll(PageRequest.of(0, 10));
    }

    @Test
    @DisplayName("findAllPaginated - returns empty page when no countries exist")
    void findAllPaginated_emptyPage() {
        Page<Country> emptyPage = new PageImpl<>(List.of());
        when(countryRepository.findAll(any(PageRequest.class))).thenReturn(emptyPage);

        Page<CountryDto.Response> result = countryService.findAllPaginated(0, 10);

        assertThat(result.getContent()).isEmpty();
    }

    // GET BY ID
    @Test
    @DisplayName("findById - returns response when country exists")
    void findById_found() {
        when(countryRepository.findById(1)).thenReturn(Optional.of(sampleCountry));

        CountryDto.Response response = countryService.findById(1);

        assertThat(response.getCountryId()).isEqualTo(1);
        assertThat(response.getCountry()).isEqualTo("India");
    }

    @Test
    @DisplayName("findById - throws ResourceNotFoundException when country not found")
    void findById_notFound() {
        when(countryRepository.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> countryService.findById(99))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    // GET BY NAME
    @Test
    @DisplayName("getCountriesByName - returns matching countries")
    void getCountriesByName_found() {
        when(countryRepository.findByCountryContainingIgnoreCase("ind"))
                .thenReturn(List.of(sampleCountry));

        List<CountryDto.Response> result = countryService.getCountriesByName("ind");

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getCountry()).isEqualTo("India");
    }

    @Test
    @DisplayName("getCountriesByName - throws ResourceNotFoundException when no match")
    void getCountriesByName_notFound() {
        when(countryRepository.findByCountryContainingIgnoreCase("xyz"))
                .thenReturn(List.of());

        assertThatThrownBy(() -> countryService.getCountriesByName("xyz"))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    // CREATE
    @Test
    @DisplayName("create - saves and returns new country")
    void create_success() {
        CountryDto.Request request = new CountryDto.Request();
        request.setCountry("Germany");

        when(countryRepository.existsByCountryIgnoreCase("Germany")).thenReturn(false);
        Country saved = new Country();
        saved.setCountryId(2);
        saved.setCountry("Germany");
        saved.setLastUpdate(LocalDateTime.now());
        when(countryRepository.save(any(Country.class))).thenReturn(saved);

        CountryDto.Response response = countryService.create(request);

        assertThat(response.getCountry()).isEqualTo("Germany");
        assertThat(response.getCountryId()).isEqualTo(2);
    }

    @Test
    @DisplayName("create - throws DuplicateResourceException when country already exists")
    void create_duplicateName() {
        CountryDto.Request request = new CountryDto.Request();
        request.setCountry("India");
        when(countryRepository.existsByCountryIgnoreCase("India")).thenReturn(true);

        assertThatThrownBy(() -> countryService.create(request))
                .isInstanceOf(DuplicateResourceException.class);

        verify(countryRepository, never()).save(any());
    }

    // UPDATE
    @Test
    @DisplayName("update - updates and returns country")
    void update_success() {
        CountryDto.Request request = new CountryDto.Request();
        request.setCountry("India Updated");

        when(countryRepository.findById(1)).thenReturn(Optional.of(sampleCountry));
        when(countryRepository.findByCountryIgnoreCase("India Updated")).thenReturn(Optional.empty());
        when(countryRepository.save(any(Country.class))).thenReturn(sampleCountry);

        CountryDto.Response response = countryService.update(1, request);

        assertThat(response).isNotNull();
        verify(countryRepository).save(sampleCountry);
    }

    @Test
    @DisplayName("update - throws DuplicateResourceException when name belongs to another record")
    void update_duplicateName() {
        Country other = new Country();
        other.setCountryId(2);
        other.setCountry("India Updated");

        CountryDto.Request request = new CountryDto.Request();
        request.setCountry("India Updated");

        when(countryRepository.findById(1)).thenReturn(Optional.of(sampleCountry));
        when(countryRepository.findByCountryIgnoreCase("India Updated"))
                .thenReturn(Optional.of(other));

        assertThatThrownBy(() -> countryService.update(1, request))
                .isInstanceOf(DuplicateResourceException.class);
    }

    @Test
    @DisplayName("update - throws ResourceNotFoundException when country does not exist")
    void update_notFound() {
        when(countryRepository.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> countryService.update(99, new CountryDto.Request()))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}
