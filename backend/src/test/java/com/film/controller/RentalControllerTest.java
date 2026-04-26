package com.film.controller;

import com.film.dto.RentalDTO;
import com.film.dto.RentalResponseDTO;
import com.film.dto.InventoryDTO;
import com.film.dto.CustomerDTO;
import com.film.dto.StaffDTO;
import com.film.services.RentalService;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;

import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.eq;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(RentalController.class)
class RentalControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RentalService service;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testGetAllRentals() throws Exception {
        RentalDTO dto = new RentalDTO();
        dto.setRentalId(1);

        when(service.getAllRentals()).thenReturn(List.of(dto));

        mockMvc.perform(get("/api/rental/getAllRental"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].rentalId").value(1));
    }

    @Test
    void testGetPagedRentals() throws Exception {
        RentalDTO dto = new RentalDTO();
        dto.setRentalId(1);

        // Controller calls service.findAllPaginated(page, size) which returns Page<RentalDTO>
        // Spring serializes Page<T> as an object with a "content" array
        Page<RentalDTO> page = new PageImpl<>(List.of(dto), PageRequest.of(0, 10), 1);

        when(service.findAllPaginated(0, 10)).thenReturn(page);

        mockMvc.perform(get("/api/rental/paged")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].rentalId").value(1));
    }

    @Test
    void testGetRentalById() throws Exception {
        RentalDTO dto = new RentalDTO();
        dto.setRentalId(1);

        when(service.getRentalById(1)).thenReturn(dto);

        mockMvc.perform(get("/api/rental/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.rentalId").value(1));
    }

    @Test
    void testGetByCustomer() throws Exception {
        RentalDTO dto = new RentalDTO();
        dto.setRentalId(1);

        when(service.getByCustomerId(1)).thenReturn(List.of(dto));

        mockMvc.perform(get("/api/rental/customer/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].rentalId").value(1));
    }

    @Test
    void testGetByInventory() throws Exception {
        RentalDTO dto = new RentalDTO();
        dto.setRentalId(1);

        when(service.getByInventoryId(1)).thenReturn(List.of(dto));

        mockMvc.perform(get("/api/rental/inventory/1"))
                .andExpect(status().isOk());
    }

    @Test
    void testGetByStaff() throws Exception {
        RentalDTO dto = new RentalDTO();
        dto.setRentalId(1);

        when(service.getByStaffId(1)).thenReturn(List.of(dto));

        mockMvc.perform(get("/api/rental/staff/1"))
                .andExpect(status().isOk());
    }

    @Test
    void testCreateRental() throws Exception {
        RentalDTO dto = new RentalDTO();
        dto.setRentalId(1);

        when(service.createRental(any(RentalDTO.class))).thenReturn(dto);

        mockMvc.perform(post("/api/rental")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.rentalId").value(1));
    }

    @Test
    void testUpdateRental() throws Exception {
        RentalDTO dto = new RentalDTO();
        dto.setRentalId(1);

        when(service.updateRental(eq(1), any(RentalDTO.class)))
                .thenReturn(dto);

        mockMvc.perform(put("/api/rental/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.rentalId").value(1));
    }

    @Test
    void testGetByDateRange() throws Exception {
        RentalDTO dto = new RentalDTO();
        dto.setRentalId(1);

        when(service.getByRentalDateRange(any(), any()))
                .thenReturn(List.of(dto));

        mockMvc.perform(get("/api/rental/date-range")
                        .param("startDate", LocalDateTime.now().toString())
                        .param("endDate", LocalDateTime.now().toString()))
                .andExpect(status().isOk());
    }

    @Test
    void testGetByReturnDateRange() throws Exception {
        RentalDTO dto = new RentalDTO();
        dto.setRentalId(1);

        LocalDateTime now = LocalDateTime.now();

        when(service.getByReturnDateRange(any(), any()))
                .thenReturn(List.of(dto));

        mockMvc.perform(get("/api/rental/return-date-range")
                        .param("startDate", now.toString())
                        .param("endDate", now.toString()))
                .andExpect(status().isOk());
    }

    @Test
    void testGetByLastUpdateRange() throws Exception {
        RentalDTO dto = new RentalDTO();
        dto.setRentalId(1);

        LocalDateTime now = LocalDateTime.now();

        when(service.getByLastUpdateRange(any(), any()))
                .thenReturn(List.of(dto));

        mockMvc.perform(get("/api/rental/last-update-range")
                        .param("startDate", now.toString())
                        .param("endDate", now.toString()))
                .andExpect(status().isOk());
    }

    @Test
    void testGetRentalInventory() throws Exception {
        RentalDTO rentalDTO = new RentalDTO();
        rentalDTO.setRentalId(1);

        InventoryDTO inventoryDTO = new InventoryDTO();
        inventoryDTO.setInventoryId(1);

        RentalResponseDTO response =
                new RentalResponseDTO(rentalDTO, inventoryDTO);

        when(service.getRentalInventory(1, 1)).thenReturn(response);

        mockMvc.perform(get("/api/rental/rental-inventory")
                        .param("rentalId", "1")
                        .param("inventoryId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"));
    }

    @Test
    void testGetRentalCustomer() throws Exception {
        RentalDTO rentalDTO = new RentalDTO();
        rentalDTO.setRentalId(1);

        CustomerDTO.Response customerDTO = new CustomerDTO.Response();
        customerDTO.setCustomerId(1);

        RentalResponseDTO response =
                new RentalResponseDTO(rentalDTO, customerDTO);

        when(service.getRentalCustomer(1, 1)).thenReturn(response);

        mockMvc.perform(get("/api/rental/rental-customer")
                        .param("rentalId", "1")
                        .param("customerId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"));
    }

    @Test
    void testGetRentalStaff() throws Exception {
        RentalDTO rentalDTO = new RentalDTO();
        rentalDTO.setRentalId(1);

        StaffDTO.Response staffDTO = new StaffDTO.Response();
        staffDTO.setStaffId(1);

        RentalResponseDTO response =
                new RentalResponseDTO(rentalDTO, staffDTO);

        when(service.getRentalStaff(1, 1)).thenReturn(response);

        mockMvc.perform(get("/api/rental/rental-staff")
                        .param("rentalId", "1")
                        .param("staffId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"));
    }
}