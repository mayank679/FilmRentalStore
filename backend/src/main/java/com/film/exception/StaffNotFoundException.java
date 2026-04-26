package com.film.exception;

public class StaffNotFoundException extends RuntimeException {
	public StaffNotFoundException (Integer id) {
		super ("Staff could not be found with id : " + id);
	}
	public StaffNotFoundException (String username) {
		super ("STaff could not be found with username : " + username);
	}
}
