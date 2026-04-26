package com.film.dto;

import lombok.Data;

@Data
public class InventoryResponseDTO
{
    private InventoryDTO inventory;
    private FilmDTO film;
    private StoreDTO.Response store;

    public InventoryResponseDTO(InventoryDTO inventory, FilmDTO film) {
        this.inventory = inventory;
        this.film = film;
    }
    public InventoryResponseDTO(InventoryDTO inventory, StoreDTO.Response store) {
        this.inventory = inventory;
        this.store = store;
    }
}
