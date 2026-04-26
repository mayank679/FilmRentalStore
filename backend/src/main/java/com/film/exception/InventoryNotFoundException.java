package com.film.exception;

public class InventoryNotFoundException extends ResourceNotFoundException {

    public InventoryNotFoundException(Integer id) {
        super("Inventory", "id", id);
    }
}