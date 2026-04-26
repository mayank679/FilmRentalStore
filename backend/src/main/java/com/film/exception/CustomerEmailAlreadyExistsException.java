package com.film.exception;

public class CustomerEmailAlreadyExistsException extends RuntimeException {
	public CustomerEmailAlreadyExistsException(String email) {
		super ("Customer with the email " + email + " already exists. ");
	}
}
