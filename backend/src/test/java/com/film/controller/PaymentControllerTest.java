package com.film.controller;

import com.film.dto.PaymentDTO;
import com.film.dto.PaymentResponseDTO;
import com.film.dto.StaffDTO;
import com.film.dto.CustomerDTO;
import com.film.dto.RentalDTO;
import com.film.services.PaymentService;

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

@WebMvcTest(PaymentController.class)
class PaymentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PaymentService service;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testGetAllPayments() throws Exception {
        PaymentDTO dto = new PaymentDTO();
        dto.setPaymentId(1);

        when(service.getAllPayments()).thenReturn(List.of(dto));

        mockMvc.perform(get("/api/payment"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].paymentId").value(1));
    }

    @Test
    void testGetById() throws Exception {
        PaymentDTO dto = new PaymentDTO();
        dto.setPaymentId(1);

        when(service.getPaymentById(1)).thenReturn(dto);

        mockMvc.perform(get("/api/payment/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.paymentId").value(1));
    }

    @Test
    void testGetByCustomer() throws Exception {
        PaymentDTO dto = new PaymentDTO();
        dto.setPaymentId(1);

        when(service.getByCustomerId(1)).thenReturn(List.of(dto));

        mockMvc.perform(get("/api/payment/customer/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].paymentId").value(1));
    }

    @Test
    void testGetByStaff() throws Exception {
        PaymentDTO dto = new PaymentDTO();
        dto.setPaymentId(1);

        when(service.getByStaffId(1)).thenReturn(List.of(dto));

        mockMvc.perform(get("/api/payment/staff/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].paymentId").value(1));
    }

    @Test
    void testGetByRental() throws Exception {
        PaymentDTO dto = new PaymentDTO();
        dto.setPaymentId(1);

        when(service.getByRentalId(1)).thenReturn(List.of(dto));

        mockMvc.perform(get("/api/payment/rental/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].paymentId").value(1));
    }

    @Test
    void testGetByDate() throws Exception {
        PaymentDTO dto = new PaymentDTO();
        dto.setPaymentId(1);

        LocalDateTime date = LocalDateTime.now();

        when(service.getByPaymentDate(any(LocalDateTime.class)))
                .thenReturn(List.of(dto));

        mockMvc.perform(get("/api/payment/date")
                        .param("date", date.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].paymentId").value(1));
    }

    @Test
    void testCreatePayment() throws Exception {
        PaymentDTO dto = new PaymentDTO();
        dto.setPaymentId(1);

        when(service.createPayment(any(PaymentDTO.class))).thenReturn(dto);

        mockMvc.perform(post("/api/payment")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.paymentId").value(1));
    }

    @Test
    void testUpdatePayment() throws Exception {
        PaymentDTO dto = new PaymentDTO();
        dto.setPaymentId(1);

        when(service.updatePayment(eq(1), any(PaymentDTO.class)))
                .thenReturn(dto);

        mockMvc.perform(put("/api/payment/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.paymentId").value(1));
    }

    @Test
    void testGetPaymentStaff() throws Exception {
        PaymentDTO paymentDTO = new PaymentDTO();
        paymentDTO.setPaymentId(1);

        StaffDTO.Response staffDTO = new StaffDTO.Response();
        staffDTO.setStaffId(1);

        PaymentResponseDTO response =
                new PaymentResponseDTO(paymentDTO, staffDTO);

        when(service.getPaymentStaff(1, 1)).thenReturn(response);

        mockMvc.perform(get("/api/payment/payment-staff")
                        .param("paymentId", "1")
                        .param("staffId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"));
    }

    @Test
    void testGetPaymentCustomer() throws Exception {
        PaymentDTO paymentDTO = new PaymentDTO();
        paymentDTO.setPaymentId(1);

        CustomerDTO.Response customerDTO = new CustomerDTO.Response();
        customerDTO.setCustomerId(1);

        PaymentResponseDTO response =
                new PaymentResponseDTO(paymentDTO, customerDTO);

        when(service.getPaymentCustomer(1, 1)).thenReturn(response);

        mockMvc.perform(get("/api/payment/payment-customer")
                        .param("paymentId", "1")
                        .param("customerId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"));
    }

    @Test
    void testGetPaymentRental() throws Exception {
        PaymentDTO paymentDTO = new PaymentDTO();
        paymentDTO.setPaymentId(1);

        RentalDTO rentalDTO = new RentalDTO();
        rentalDTO.setRentalId(1);

        PaymentResponseDTO response =
                new PaymentResponseDTO(paymentDTO, rentalDTO);

        when(service.getPaymentRental(1, 1)).thenReturn(response);

        mockMvc.perform(get("/api/payment/payment-rental")
                        .param("paymentId", "1")
                        .param("rentalId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"));
    }
}