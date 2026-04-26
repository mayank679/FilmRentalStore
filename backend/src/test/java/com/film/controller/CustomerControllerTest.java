package com.film.controller;

import com.film.dto.CustomerDTO;
import com.film.services.CustomerService;

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

@WebMvcTest(CustomerController.class)
class CustomerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CustomerService customerService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testGetAllCustomers() throws Exception {
        CustomerDTO.Response response = new CustomerDTO.Response();
        response.setCustomerId(1);

        when(customerService.getAllCustomers()).thenReturn(List.of(response));

        mockMvc.perform(get("/api/customers"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].customerId").value(1));
    }

    @Test
    void testGetCustomerById() throws Exception {
        CustomerDTO.Response response = new CustomerDTO.Response();
        response.setCustomerId(1);

        when(customerService.getCustomerById(1)).thenReturn(response);

        mockMvc.perform(get("/api/customers/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.customerId").value(1));
    }

    @Test
    void testGetCustomerByStore() throws Exception {
        CustomerDTO.Response response = new CustomerDTO.Response();
        response.setCustomerId(1);

        when(customerService.getCustomerByStore(1)).thenReturn(List.of(response));

        mockMvc.perform(get("/api/customers/store/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].customerId").value(1));
    }

    @Test
    void testCreateCustomer() throws Exception {
        CustomerDTO.Request request = new CustomerDTO.Request();
        request.setFirstName("John");

        CustomerDTO.Response response = new CustomerDTO.Response();
        response.setCustomerId(1);

        when(customerService.createCustomer(any(CustomerDTO.Request.class)))
                .thenReturn(response);

        mockMvc.perform(post("/api/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.customerId").value(1));
    }

    @Test
    void testUpdateCustomer() throws Exception {
        CustomerDTO.Request request = new CustomerDTO.Request();
        request.setFirstName("Updated");

        CustomerDTO.Response response = new CustomerDTO.Response();
        response.setCustomerId(1);

        when(customerService.updateCustomer(eq(1), any(CustomerDTO.Request.class)))
                .thenReturn(response);

        mockMvc.perform(put("/api/customers/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.customerId").value(1));
    }
}