package com.film.exception;

public class RentalNotFoundException extends ResourceNotFoundException {

    public RentalNotFoundException(Integer id) {
        super("Rental", "id", id);
    }
}