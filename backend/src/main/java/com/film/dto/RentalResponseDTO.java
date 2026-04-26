package com.film.dto;

import lombok.Data;

@Data
public class RentalResponseDTO
{
    private RentalDTO rental;
    private InventoryDTO inventory;
    private CustomerDTO.Response customer;
    private StaffDTO.Response staff;

    public RentalResponseDTO(RentalDTO rental, InventoryDTO inventory) {
        this.rental = rental;
        this.inventory = inventory;
    }

    public RentalResponseDTO(RentalDTO rental, CustomerDTO.Response customer) {
        this.rental = rental;
        this.customer = customer;
    }

    public RentalResponseDTO(RentalDTO rental, StaffDTO.Response staff) {
        this.rental = rental;
        this.staff = staff;
    }
}
