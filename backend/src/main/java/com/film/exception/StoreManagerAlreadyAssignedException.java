package com.film.exception;

public class StoreManagerAlreadyAssignedException extends RuntimeException {
	public StoreManagerAlreadyAssignedException(Integer staffId) {
		super("staff_id : " + staffId + " is already managing a different store.");
	}
}
