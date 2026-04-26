package com.film.service;

import com.film.dto.AddressDto;
import com.film.entity.Address;
import com.film.entity.City;
import com.film.entity.Country;
import com.film.exception.ResourceNotFoundException;
import com.film.repository.AddressRepository;
import com.film.repository.CityRepository;
import com.film.services.AddressService;
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
@DisplayName("AddressService Unit Tests")
class AddressServiceTest {

    @Mock
    private AddressRepository addressRepository;

    @Mock
    private CityRepository cityRepository;

    @InjectMocks
    private AddressService addressService;

    private Country sampleCountry;
    private City sampleCity;
    private Address sampleAddress;

    @BeforeEach
    void setUp() {
        sampleCountry = new Country();
        sampleCountry.setCountryId(1);
        sampleCountry.setCountry("India");

        sampleCity = new City();
        sampleCity.setCityId(10);
        sampleCity.setCity("Kolkata");
        sampleCity.setCountry(sampleCountry);

        sampleAddress = Address.builder()
                .addressId(100)
                .address("12 Park Street")
                .district("West Bengal")
                .city(sampleCity)
                .postalCode("700016")
                .phone("033-12345678")
                .location(null)
                .lastUpdate(LocalDateTime.now())
                .build();
    }

    // GET ALL
    @Test
    @DisplayName("findAllPaginated - returns mapped page")
    void findAllPaginated_returnsPage() {
        Page<Address> page = new PageImpl<>(List.of(sampleAddress));
        when(addressRepository.findAll(any(PageRequest.class))).thenReturn(page);

        Page<AddressDto.Response> result = addressService.findAllPaginated(0, 10);

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getAddress()).isEqualTo("12 Park Street");
        assertThat(result.getContent().get(0).getCityId()).isEqualTo(10);
    }

    // GET BY ID
    @Test
    @DisplayName("findById - returns response when address exists")
    void findById_found() {
        when(addressRepository.findById(100)).thenReturn(Optional.of(sampleAddress));

        AddressDto.Response response = addressService.findById(100);

        assertThat(response.getAddressId()).isEqualTo(100);
        assertThat(response.getDistrict()).isEqualTo("West Bengal");
    }

    @Test
    @DisplayName("findById - throws ResourceNotFoundException when address not found")
    void findById_notFound() {
        when(addressRepository.findById(999)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> addressService.findById(999))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    // GET BY CITY ID
    // FIX: method returns List<AddressDto.DetailedResponse> — update variable type and assertions
    @Test
    @DisplayName("findByCityId - returns DetailedResponse list with cityName and countryName")
    void findByCityId_returnsList() {
        when(addressRepository.findByCity_CityId(10)).thenReturn(List.of(sampleAddress));

        List<AddressDto.DetailedResponse> result = addressService.findByCityId(10);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getCityId()).isEqualTo(10);
        assertThat(result.get(0).getCityName()).isEqualTo("Kolkata");
        assertThat(result.get(0).getCountryId()).isEqualTo(1);
        assertThat(result.get(0).getCountryName()).isEqualTo("India");
    }

    @Test
    @DisplayName("findByCityId - returns empty list when no addresses found")
    void findByCityId_empty() {
        when(addressRepository.findByCity_CityId(99)).thenReturn(List.of());

        // FIX: assign to List<AddressDto.DetailedResponse>
        List<AddressDto.DetailedResponse> result = addressService.findByCityId(99);

        assertThat(result).isEmpty();
    }

    // GET BY DISTRICT
    @Test
    @DisplayName("findByDistrict - returns addresses matching district")
    void findByDistrict_returnsList() {
        when(addressRepository.findByDistrictIgnoreCase("West Bengal"))
                .thenReturn(List.of(sampleAddress));

        List<AddressDto.Response> result = addressService.findByDistrict("West Bengal");

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getDistrict()).isEqualTo("West Bengal");
    }

    // GET BY POSTAL CODE
    @Test
    @DisplayName("findByPostalCode - returns addresses matching postal code")
    void findByPostalCode_returnsList() {
        when(addressRepository.findByPostalCode("700016")).thenReturn(List.of(sampleAddress));

        List<AddressDto.Response> result = addressService.findByPostalCode("700016");

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getPostalCode()).isEqualTo("700016");
    }

    // GET BY PHONE
    @Test
    @DisplayName("findByPhone - returns addresses matching phone")
    void findByPhone_returnsList() {
        when(addressRepository.findByPhone("033-12345678")).thenReturn(List.of(sampleAddress));

        List<AddressDto.Response> result = addressService.findByPhone("033-12345678");

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getPhone()).isEqualTo("033-12345678");
    }

    // GET WITH LOCATION
    @Test
    @DisplayName("findAllWithLocation - returns addresses that have a location")
    void findAllWithLocation_returnsList() {
        when(addressRepository.findAllWithLocation()).thenReturn(List.of());

        List<AddressDto.Response> result = addressService.findAllWithLocation();

        assertThat(result).isNotNull();
        verify(addressRepository).findAllWithLocation();
    }

    // CREATE
    @Test
    @DisplayName("create - saves and returns new address")
    void create_success() {
        AddressDto.Request request = new AddressDto.Request();
        request.setAddress("55 Main Road");
        request.setDistrict("Maharashtra");
        request.setCityId(10);
        request.setPostalCode("400001");
        request.setPhone("022-99887766");

        when(cityRepository.findById(10)).thenReturn(Optional.of(sampleCity));
        when(addressRepository.save(any(Address.class))).thenReturn(sampleAddress);

        AddressDto.Response response = addressService.create(request);

        assertThat(response).isNotNull();
        verify(addressRepository).save(any(Address.class));
    }

    @Test
    @DisplayName("create - throws ResourceNotFoundException when city not found")
    void create_cityNotFound() {
        AddressDto.Request request = new AddressDto.Request();
        request.setCityId(99);

        when(cityRepository.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> addressService.create(request))
                .isInstanceOf(ResourceNotFoundException.class);

        verify(addressRepository, never()).save(any());
    }

    @Test
    @DisplayName("create - throws RuntimeException for invalid WKT location format")
    void create_invalidLocation() {
        AddressDto.Request request = new AddressDto.Request();
        request.setAddress("55 Main Road");
        request.setDistrict("Maharashtra");
        request.setCityId(10);
        request.setPhone("022-99887766");
        request.setLocation("INVALID_WKT");

        when(cityRepository.findById(10)).thenReturn(Optional.of(sampleCity));

        assertThatThrownBy(() -> addressService.create(request))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Invalid location format");
    }

    // UPDATE
    @Test
    @DisplayName("update - updates and returns address")
    void update_success() {
        AddressDto.Request request = new AddressDto.Request();
        request.setAddress("99 New Road");
        request.setDistrict("Updated District");
        request.setCityId(10);
        request.setPhone("099-11223344");

        when(addressRepository.findById(100)).thenReturn(Optional.of(sampleAddress));
        when(cityRepository.findById(10)).thenReturn(Optional.of(sampleCity));
        when(addressRepository.save(any(Address.class))).thenReturn(sampleAddress);

        AddressDto.Response response = addressService.update(100, request);

        assertThat(response).isNotNull();
        verify(addressRepository).save(sampleAddress);
    }

    @Test
    @DisplayName("update - throws ResourceNotFoundException when address not found")
    void update_notFound() {
        when(addressRepository.findById(999)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> addressService.update(999, new AddressDto.Request()))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    @DisplayName("update - throws ResourceNotFoundException when city not found")
    void update_cityNotFound() {
        AddressDto.Request request = new AddressDto.Request();
        request.setCityId(99);

        when(addressRepository.findById(100)).thenReturn(Optional.of(sampleAddress));
        when(cityRepository.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> addressService.update(100, request))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}