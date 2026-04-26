package com.film.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.film.dto.AddressDto;
import com.film.exception.ResourceNotFoundException;
import com.film.services.AddressService;
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

@WebMvcTest(AddressController.class)
@DisplayName("AddressController MockMvc Tests")
class AddressControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AddressService addressService;

    @Autowired
    private ObjectMapper objectMapper;

    private AddressDto.Response buildResponse(int id) {
        return AddressDto.Response.builder()
                .addressId(id)
                .address("12 Park Street")
                .district("West Bengal")
                .cityId(10)
                .postalCode("700016")
                .phone("033-12345678")
                .location(null)
                .lastUpdate(LocalDateTime.now())
                .build();
    }

    // FIX: buildDetailedResponse returns AddressDto.DetailedResponse for FK-based endpoints
    private AddressDto.DetailedResponse buildDetailedResponse(int id) {
        return AddressDto.DetailedResponse.builder()
                .addressId(id)
                .address("12 Park Street")
                .district("West Bengal")
                .cityId(10)
                .cityName("Kolkata")
                .countryId(1)
                .countryName("India")
                .postalCode("700016")
                .phone("033-12345678")
                .location(null)
                .lastUpdate(LocalDateTime.now())
                .build();
    }

    private AddressDto.Request buildRequest() {
        AddressDto.Request req = new AddressDto.Request();
        req.setAddress("12 Park Street");
        req.setDistrict("West Bengal");
        req.setCityId(10);
        req.setPostalCode("700016");
        req.setPhone("033-12345678");
        return req;
    }

    // GET ALL
    @Test
    @DisplayName("GET /api/addresses - returns 200 with paginated addresses")
    void getAll_returns200() throws Exception {
        Page<AddressDto.Response> page = new PageImpl<>(List.of(buildResponse(100)));
        when(addressService.findAllPaginated(0, 10)).thenReturn(page);

        mockMvc.perform(get("/api/addresses").param("page", "0").param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].addressId").value(100))
                .andExpect(jsonPath("$.content[0].address").value("12 Park Street"));
    }

    // GET BY ID
    @Test
    @DisplayName("GET /api/addresses/{id} - returns 200 when found")
    void getById_found() throws Exception {
        when(addressService.findById(100)).thenReturn(buildResponse(100));

        mockMvc.perform(get("/api/addresses/100"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.addressId").value(100))
                .andExpect(jsonPath("$.district").value("West Bengal"));
    }

    @Test
    @DisplayName("GET /api/addresses/{id} - returns 404 when not found")
    void getById_notFound() throws Exception {
        when(addressService.findById(999))
                .thenThrow(new ResourceNotFoundException("Address", "id", 999));

        mockMvc.perform(get("/api/addresses/999"))
                .andExpect(status().isNotFound());
    }

    // GET BY CITY ID
    // FIX: stub must return List<AddressDto.DetailedResponse> to match service signature
    // FIX: assert on DetailedResponse fields — cityName and countryName are now available
    @Test
    @DisplayName("GET /api/addresses/city/{cityId} - returns list of addresses with city and country info")
    void getByCityId_found() throws Exception {
        when(addressService.findByCityId(10))
                .thenReturn(List.of(buildDetailedResponse(100)));

        mockMvc.perform(get("/api/addresses/city/10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].cityId").value(10))
                .andExpect(jsonPath("$[0].cityName").value("Kolkata"))
                .andExpect(jsonPath("$[0].countryName").value("India"));
    }

    @Test
    @DisplayName("GET /api/addresses/city/{cityId} - returns empty array when none found")
    void getByCityId_empty() throws Exception {
        // FIX: return empty List<AddressDto.DetailedResponse>, not List<AddressDto.Response>
        when(addressService.findByCityId(99)).thenReturn(List.of());

        mockMvc.perform(get("/api/addresses/city/99"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());
    }

    // GET BY DISTRICT
    @Test
    @DisplayName("GET /api/addresses/district/{district} - returns matching addresses")
    void getByDistrict_found() throws Exception {
        when(addressService.findByDistrict("West Bengal"))
                .thenReturn(List.of(buildResponse(100)));

        mockMvc.perform(get("/api/addresses/district/West Bengal"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].district").value("West Bengal"));
    }

    // GET BY POSTAL CODE
    @Test
    @DisplayName("GET /api/addresses/postal/{postalCode} - returns matching addresses")
    void getByPostalCode_found() throws Exception {
        when(addressService.findByPostalCode("700016"))
                .thenReturn(List.of(buildResponse(100)));

        mockMvc.perform(get("/api/addresses/postal/700016"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].postalCode").value("700016"));
    }

    // GET BY PHONE
    @Test
    @DisplayName("GET /api/addresses/phone/{phone} - returns matching addresses")
    void getByPhone_found() throws Exception {
        when(addressService.findByPhone("033-12345678"))
                .thenReturn(List.of(buildResponse(100)));

        mockMvc.perform(get("/api/addresses/phone/033-12345678"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].phone").value("033-12345678"));
    }

    // GET WITH LOCATION
    @Test
    @DisplayName("GET /api/addresses/location - returns addresses with location")
    void getWithLocation_returnsOk() throws Exception {
        when(addressService.findAllWithLocation()).thenReturn(List.of());

        mockMvc.perform(get("/api/addresses/location"))
                .andExpect(status().isOk());
    }

    // POST
    @Test
    @DisplayName("POST /api/addresses - returns 201 and created address")
    void create_success() throws Exception {
        when(addressService.create(any(AddressDto.Request.class)))
                .thenReturn(buildResponse(101));

        mockMvc.perform(post("/api/addresses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(buildRequest())))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.addressId").value(101))
                .andExpect(jsonPath("$.address").value("12 Park Street"));
    }

    @Test
    @DisplayName("POST /api/addresses - returns 404 when city not found")
    void create_cityNotFound() throws Exception {
        when(addressService.create(any(AddressDto.Request.class)))
                .thenThrow(new ResourceNotFoundException("City", "id", 99));

        mockMvc.perform(post("/api/addresses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(buildRequest())))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("POST /api/addresses - returns 400 when required fields are blank")
    void create_validationFails() throws Exception {
        AddressDto.Request req = new AddressDto.Request();
        // address, district, phone left blank — all @NotBlank

        mockMvc.perform(post("/api/addresses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /api/addresses - returns 400 when location format is invalid")
    void create_invalidLocation() throws Exception {
        AddressDto.Request req = buildRequest();
        req.setLocation("NOT_VALID_WKT");  // fails @Pattern

        mockMvc.perform(post("/api/addresses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest());
    }

    // PUT
    @Test
    @DisplayName("PUT /api/addresses/{id} - returns 200 and updated address")
    void update_success() throws Exception {
        when(addressService.update(eq(100), any(AddressDto.Request.class)))
                .thenReturn(buildResponse(100));

        mockMvc.perform(put("/api/addresses/100")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(buildRequest())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.addressId").value(100));
    }

    @Test
    @DisplayName("PUT /api/addresses/{id} - returns 404 when address not found")
    void update_notFound() throws Exception {
        when(addressService.update(eq(999), any(AddressDto.Request.class)))
                .thenThrow(new ResourceNotFoundException("Address", "id", 999));

        mockMvc.perform(put("/api/addresses/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(buildRequest())))
                .andExpect(status().isNotFound());
    }
}