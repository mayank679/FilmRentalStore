package com.film.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class FilmNotFoundException extends RuntimeException {

    public FilmNotFoundException(String message) {
        super(message);
    }

    public FilmNotFoundException(Integer id) {
        super("Film not found with ID: " + id);
    }

    public FilmNotFoundException(Integer id, String rating) {
        super("No film found with ID: " + id + " and rating: " + rating);
    }
}