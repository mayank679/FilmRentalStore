package com.film.controller;

import com.film.dto.InventoryDTO;
import com.film.dto.InventoryResponseDTO;
import com.film.dto.FilmDTO;
import com.film.dto.StoreDTO;
import com.film.services.InventoryService;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;

import org.springframework.boot.test.mock.mockito.MockBean;
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

@WebMvcTest(InventoryController.class)
class InventoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private InventoryService service;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testGetAllInventory() throws Exception {
        InventoryDTO dto = new InventoryDTO();
        dto.setInventoryId(1);

        when(service.getAllInventory()).thenReturn(List.of(dto));

        mockMvc.perform(get("/api/inventory"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].inventoryId").value(1));
    }

    @Test
    void testGetInventoryById() throws Exception {
        InventoryDTO dto = new InventoryDTO();
        dto.setInventoryId(1);

        when(service.getInventoryById(1)).thenReturn(dto);

        mockMvc.perform(get("/api/inventory/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.inventoryId").value(1));
    }

    @Test
    void testGetByFilmId() throws Exception {
        InventoryDTO dto = new InventoryDTO();
        dto.setInventoryId(1);

        when(service.getByFilmId(1)).thenReturn(List.of(dto));

        mockMvc.perform(get("/api/inventory/film/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].inventoryId").value(1));
    }

    @Test
    void testGetByStoreId() throws Exception {
        InventoryDTO dto = new InventoryDTO();
        dto.setInventoryId(1);

        when(service.getByStoreId(1)).thenReturn(List.of(dto));

        mockMvc.perform(get("/api/inventory/store/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].inventoryId").value(1));
    }

    @Test
    void testGetPaginatedInventory() throws Exception {
        InventoryDTO dto = new InventoryDTO();
        dto.setInventoryId(1);

        when(service.getInventoryWithPagination(any()))
                .thenReturn(List.of(dto));

        mockMvc.perform(get("/api/inventory/inventory_page")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].inventoryId").value(1));
    }

    @Test
    void testGetByLastUpdate() throws Exception {
        InventoryDTO dto = new InventoryDTO();
        dto.setInventoryId(1);

        LocalDateTime time = LocalDateTime.now();

        when(service.getByLastUpdate(any(LocalDateTime.class)))
                .thenReturn(List.of(dto));

        mockMvc.perform(get("/api/inventory/last-update")
                        .param("lastUpdate", time.toString()))
                .andExpect(status().isOk());
    }

    @Test
    void testCreateInventory() throws Exception {
        InventoryDTO dto = new InventoryDTO();
        dto.setInventoryId(1);

        when(service.createInventory(any(InventoryDTO.class))).thenReturn(dto);

        mockMvc.perform(post("/api/inventory")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.inventoryId").value(1));
    }

    @Test
    void testUpdateInventory() throws Exception {
        InventoryDTO dto = new InventoryDTO();
        dto.setInventoryId(1);

        when(service.updateInventory(eq(1), any(InventoryDTO.class)))
                .thenReturn(dto);

        mockMvc.perform(put("/api/inventory/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.inventoryId").value(1));
    }

    @Test
    void testGetInventoryStore() throws Exception {
        InventoryDTO inventoryDTO = new InventoryDTO();
        inventoryDTO.setInventoryId(1);

        StoreDTO.Response storeDTO = new StoreDTO.Response();
        storeDTO.setStoreId(1);

        InventoryResponseDTO response =
                new InventoryResponseDTO(inventoryDTO, storeDTO);

        when(service.getInventoryStore(1, 1)).thenReturn(response);

        mockMvc.perform(get("/api/inventory/inventory-store")
                        .param("inventoryId", "1")
                        .param("storeId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"));
    }

    @Test
    void testGetInventoryFilm() throws Exception {
        InventoryDTO inventoryDTO = new InventoryDTO();
        inventoryDTO.setInventoryId(1);

        FilmDTO filmDTO = new FilmDTO();
        filmDTO.setFilmId(1);

        InventoryResponseDTO response =
                new InventoryResponseDTO(inventoryDTO, filmDTO);

        when(service.getInventoryFilm(1, 1)).thenReturn(response);

        mockMvc.perform(get("/api/inventory/inventory-film")
                        .param("inventoryId", "1")
                        .param("filmId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"));
    }
}