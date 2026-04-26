package com.film.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

public class CustomerDTO {
	
	@Getter
	@Setter
	@NoArgsConstructor
	@AllArgsConstructor
	@Builder
	
	public static class Request {
		
		private Integer storeId;
		private String firstName;
		private String lastName; 
		private String email;
		private Integer addressId;
		private Boolean active;
		
	}
	
	@Getter
	@Setter
	@NoArgsConstructor
	@AllArgsConstructor
	@Builder
	
	public static class PatchRequest {
		
		private String firstName;
		private String lastName; 
		private String email;
		private Integer addressId;
		private Boolean active;
	}
	
	@Getter
	@Setter
	@NoArgsConstructor
	@AllArgsConstructor
	@Builder
	
	public static class Response {
		
		private Integer customerId;
		private Integer storeId;
		private String firstName;
		private String lastName; 
		private String email;
		private Integer addressId;
		private Boolean active;
		private LocalDateTime createDate;
		private LocalDateTime lastUpdate;
	}
}