package com.film.controller;

import com.film.dto.StaffDTO;
import com.film.services.StaffService;

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

@WebMvcTest(StaffController.class)
class StaffControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private StaffService staffService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testGetAllStaff() throws Exception {
        StaffDTO.Response res = new StaffDTO.Response();
        res.setStaffId(1);

        when(staffService.getAllStaff()).thenReturn(List.of(res));

        mockMvc.perform(get("/api/staff"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].staffId").value(1));
    }

    @Test
    void testGetStaffById() throws Exception {
        StaffDTO.Response res = new StaffDTO.Response();
        res.setStaffId(1);

        when(staffService.getStaffById(1)).thenReturn(res);

        mockMvc.perform(get("/api/staff/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.staffId").value(1));
    }

    @Test
    void testGetStaffByStore() throws Exception {
        StaffDTO.Response res = new StaffDTO.Response();
        res.setStaffId(1);

        when(staffService.getStaffByStore(1)).thenReturn(List.of(res));

        mockMvc.perform(get("/api/staff/store/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].staffId").value(1));
    }

    @Test
    void testGetStaffByActive() throws Exception {
        StaffDTO.Response res = new StaffDTO.Response();
        res.setStaffId(1);

        when(staffService.getStaffByActive(true)).thenReturn(List.of(res));

        mockMvc.perform(get("/api/staff/filter")
                        .param("active", "true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].staffId").value(1));
    }

    @Test
    void testGetStaffByUsername() throws Exception {
        StaffDTO.Response res = new StaffDTO.Response();
        res.setStaffId(1);

        when(staffService.getStaffByUsername("john")).thenReturn(res);

        mockMvc.perform(get("/api/staff/username/john"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.staffId").value(1));
    }

    @Test
    void testCreateStaff() throws Exception {
        StaffDTO.Request request = new StaffDTO.Request();
        request.setUsername("john");

        StaffDTO.Response res = new StaffDTO.Response();
        res.setStaffId(1);

        when(staffService.createStaff(any(StaffDTO.Request.class)))
                .thenReturn(res);

        mockMvc.perform(post("/api/staff")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.staffId").value(1));
    }

    @Test
    void testUpdateStaff() throws Exception {
        StaffDTO.Request request = new StaffDTO.Request();
        request.setUsername("updated");

        StaffDTO.Response res = new StaffDTO.Response();
        res.setStaffId(1);

        when(staffService.updateStaff(eq(1), any(StaffDTO.Request.class)))
                .thenReturn(res);

        mockMvc.perform(put("/api/staff/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.staffId").value(1));
    }

    @Test
    void testPatchStaff() throws Exception {
        StaffDTO.PatchRequest request = new StaffDTO.PatchRequest();
        request.setFirstName("Updated");

        StaffDTO.Response res = new StaffDTO.Response();
        res.setStaffId(1);

        when(staffService.patchStaff(eq(1), any(StaffDTO.PatchRequest.class)))
                .thenReturn(res);

        mockMvc.perform(patch("/api/staff/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.staffId").value(1));
    }
}