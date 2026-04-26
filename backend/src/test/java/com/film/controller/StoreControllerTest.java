package com.film.controller;

import com.film.dto.StoreDTO;
import com.film.services.StoreService;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;

import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.eq;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(StoreController.class)
class StoreControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private StoreService storeService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testGetAllStores() throws Exception {
        StoreDTO.Response res = new StoreDTO.Response();
        res.setStoreId(1);

        when(storeService.getAllStores()).thenReturn(List.of(res));

        mockMvc.perform(get("/api/stores"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].storeId").value(1));
    }

    @Test
    void testGetStoreById() throws Exception {
        StoreDTO.Response res = new StoreDTO.Response();
        res.setStoreId(1);

        when(storeService.getStoreById(1)).thenReturn(res);

        mockMvc.perform(get("/api/stores/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.storeId").value(1));
    }

    @Test
    void testCreateStore() throws Exception {
        StoreDTO.Request request = new StoreDTO.Request();
        request.setManagerStaffId(1);

        StoreDTO.Response res = new StoreDTO.Response();
        res.setStoreId(1);

        when(storeService.createStore(any(StoreDTO.Request.class)))
                .thenReturn(res);

        mockMvc.perform(post("/api/stores")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.storeId").value(1));
    }

    @Test
    void testUpdateStore() throws Exception {
        StoreDTO.Request request = new StoreDTO.Request();
        request.setManagerStaffId(1);

        StoreDTO.Response res = new StoreDTO.Response();
        res.setStoreId(1);

        when(storeService.updateStore(eq(1), any(StoreDTO.Request.class)))
                .thenReturn(res);

        mockMvc.perform(put("/api/stores/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.storeId").value(1));
    }
}