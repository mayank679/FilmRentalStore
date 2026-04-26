package com.film.exception;

public class StaffUsernameAlreadyExistsException extends RuntimeException {
	public StaffUsernameAlreadyExistsException (String userName) {
		super ("Staff with username " + userName + " already exists.");
	}
}
