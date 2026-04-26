package com.film.service;

import com.film.dto.CityDto;
import com.film.entity.City;
import com.film.entity.Country;
import com.film.exception.DuplicateResourceException;
import com.film.exception.ResourceNotFoundException;
import com.film.repository.CityRepository;
import com.film.repository.CountryRepository;
import com.film.services.CityService;
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
@DisplayName("CityService Unit Tests")
class CityServiceTest {

    @Mock
    private CityRepository cityRepository;

    @Mock
    private CountryRepository countryRepository;

    @InjectMocks
    private CityService cityService;

    private Country sampleCountry;
    private City sampleCity;

    @BeforeEach
    void setUp() {
        sampleCountry = new Country();
        sampleCountry.setCountryId(1);
        sampleCountry.setCountry("India");
        sampleCountry.setLastUpdate(LocalDateTime.now());

        sampleCity = new City();
        sampleCity.setCityId(10);
        sampleCity.setCity("Kolkata");
        sampleCity.setCountry(sampleCountry);
        sampleCity.setLastUpdate(LocalDateTime.now());
    }

    // GET ALL
    @Test
    @DisplayName("findAllPaginated - returns mapped page")
    void findAllPaginated_returnsMappedPage() {
        Page<City> page = new PageImpl<>(List.of(sampleCity));
        when(cityRepository.findAll(any(PageRequest.class))).thenReturn(page);

        Page<CityDto.Response> result = cityService.findAllPaginated(0, 10);

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getCity()).isEqualTo("Kolkata");
        assertThat(result.getContent().get(0).getCountryId()).isEqualTo(1);
    }

    // GET BY ID
    @Test
    @DisplayName("findById - returns response when city exists")
    void findById_found() {
        when(cityRepository.findById(10)).thenReturn(Optional.of(sampleCity));

        CityDto.Response response = cityService.findById(10);

        assertThat(response.getCityId()).isEqualTo(10);
        assertThat(response.getCity()).isEqualTo("Kolkata");
    }

    @Test
    @DisplayName("findById - throws ResourceNotFoundException when city not found")
    void findById_notFound() {
        when(cityRepository.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> cityService.findById(99))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    // GET BY NAME
    @Test
    @DisplayName("getCitiesByName - returns list when match found")
    void getCitiesByName_found() {
        when(cityRepository.findByCityContainingIgnoreCase("kol"))
                .thenReturn(List.of(sampleCity));

        List<CityDto.Response> result = cityService.getCitiesByName("kol");

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getCity()).isEqualTo("Kolkata");
    }

    @Test
    @DisplayName("getCitiesByName - throws ResourceNotFoundException when no match")
    void getCitiesByName_notFound() {
        when(cityRepository.findByCityContainingIgnoreCase("xyz"))
                .thenReturn(List.of());

        assertThatThrownBy(() -> cityService.getCitiesByName("xyz"))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    // GET BY COUNTRY ID
    // FIX: method returns List<CityDto.DetailedResponse> — update variable type and assertions
    @Test
    @DisplayName("findByCountryId - returns DetailedResponse list with countryName for given country")
    void findByCountryId_returnsCities() {
        when(cityRepository.findByCountry_CountryId(1)).thenReturn(List.of(sampleCity));

        List<CityDto.DetailedResponse> result = cityService.findByCountryId(1);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getCountryId()).isEqualTo(1);
        assertThat(result.get(0).getCountryName()).isEqualTo("India");
    }

    @Test
    @DisplayName("findByCountryId - returns empty list when no cities found")
    void findByCountryId_empty() {
        when(cityRepository.findByCountry_CountryId(99)).thenReturn(List.of());

        // FIX: assign to List<CityDto.DetailedResponse>
        List<CityDto.DetailedResponse> result = cityService.findByCountryId(99);

        assertThat(result).isEmpty();
    }

    // CREATE
    @Test
    @DisplayName("create - saves and returns new city")
    void create_success() {
        CityDto.Request request = new CityDto.Request();
        request.setCity("Mumbai");
        request.setCountryId(1);

        when(countryRepository.findById(1)).thenReturn(Optional.of(sampleCountry));
        when(cityRepository.existsByCityIgnoreCaseAndCountry_CountryId("Mumbai", 1))
                .thenReturn(false);

        City saved = new City();
        saved.setCityId(11);
        saved.setCity("Mumbai");
        saved.setCountry(sampleCountry);
        saved.setLastUpdate(LocalDateTime.now());
        when(cityRepository.save(any(City.class))).thenReturn(saved);

        CityDto.Response response = cityService.create(request);

        assertThat(response.getCity()).isEqualTo("Mumbai");
        assertThat(response.getCityId()).isEqualTo(11);
    }

    @Test
    @DisplayName("create - throws DuplicateResourceException when city exists in country")
    void create_duplicate() {
        CityDto.Request request = new CityDto.Request();
        request.setCity("Kolkata");
        request.setCountryId(1);

        when(countryRepository.findById(1)).thenReturn(Optional.of(sampleCountry));
        when(cityRepository.existsByCityIgnoreCaseAndCountry_CountryId("Kolkata", 1))
                .thenReturn(true);

        assertThatThrownBy(() -> cityService.create(request))
                .isInstanceOf(DuplicateResourceException.class);

        verify(cityRepository, never()).save(any());
    }

    @Test
    @DisplayName("create - throws ResourceNotFoundException when country not found")
    void create_countryNotFound() {
        CityDto.Request request = new CityDto.Request();
        request.setCity("NewCity");
        request.setCountryId(99);

        when(countryRepository.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> cityService.create(request))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    // UPDATE
    @Test
    @DisplayName("update - updates and returns city")
    void update_success() {
        CityDto.Request request = new CityDto.Request();
        request.setCity("Kolkata Updated");
        request.setCountryId(1);

        when(cityRepository.findById(10)).thenReturn(Optional.of(sampleCity));
        when(countryRepository.findById(1)).thenReturn(Optional.of(sampleCountry));
        when(cityRepository.findByCityIgnoreCase("Kolkata Updated")).thenReturn(Optional.empty());
        when(cityRepository.save(any(City.class))).thenReturn(sampleCity);

        CityDto.Response response = cityService.update(10, request);

        assertThat(response).isNotNull();
        verify(cityRepository).save(sampleCity);
    }

    @Test
    @DisplayName("update - throws ResourceNotFoundException when city not found")
    void update_cityNotFound() {
        when(cityRepository.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> cityService.update(99, new CityDto.Request()))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}