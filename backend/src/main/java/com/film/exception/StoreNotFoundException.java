package com.film.exception;

public class StoreNotFoundException extends RuntimeException {
	public StoreNotFoundException(Integer id) {
		super ("Store Not found with ID: " + id);
	}
}