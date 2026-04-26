package com.film.dto;

import lombok.Data;

@Data
public class PaymentResponseDTO
{
    private PaymentDTO payment;
    private StaffDTO.Response staff;
    private CustomerDTO.Response customer;
    private RentalDTO rental;

    public PaymentResponseDTO(PaymentDTO payment, StaffDTO.Response staff) {
        this.payment = payment;
        this.staff = staff;
    }

    public PaymentResponseDTO(PaymentDTO payment, CustomerDTO.Response customer) {
        this.payment = payment;
        this.customer = customer;
    }

    public PaymentResponseDTO(PaymentDTO payment, RentalDTO rental) {
        this.payment = payment;
        this.rental = rental;
    }
}
