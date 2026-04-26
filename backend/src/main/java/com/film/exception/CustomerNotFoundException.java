package com.film.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class CustomerNotFoundException extends RuntimeException {
	public CustomerNotFoundException(Integer id) {
		super("Customer could not be found with id : " + id);
	}
}
