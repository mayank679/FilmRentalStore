package com.film.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

public class StaffDTO {
	
	@Getter
	@Setter
	@AllArgsConstructor
	@NoArgsConstructor
	@Builder
	
	public static class Request {
		private String firstName;
		private String lastName;
		private Integer addressId;
		private String email;
		private Integer storeId;
		private Boolean active;
		private String username;
		private String password;
	}
	
	@Getter
	@Setter
	@AllArgsConstructor
	@NoArgsConstructor
	@Builder
	
	public static class PatchRequest {
		private String firstName;
		private String lastName;
		private String email;
		private Boolean active;
		private String username;
		private String password;
		private Integer storeId;
		private Integer addressId;
		
	}
	
	@Getter
	@Setter
	@AllArgsConstructor
	@NoArgsConstructor
	@Builder
	
	public static class Response {
		private Integer staffId;
		private String firstName;
		private String lastName;
		private Integer addressId;
		private String email;
		private Integer storeId;
		private Boolean active;
		private String username;
		private LocalDateTime lastUpdate;
		//Password not to be included in Response
	}
}
