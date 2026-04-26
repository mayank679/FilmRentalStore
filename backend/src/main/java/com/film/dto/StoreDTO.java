package com.film.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

public class StoreDTO {
	
	@Getter 
	@Setter
	@NoArgsConstructor
	@AllArgsConstructor
	@Builder
	
	public static class Request {
		private Integer managerStaffId;
		private Integer addressId;
	}
	
	@Getter 
	@Setter
	@NoArgsConstructor
	@AllArgsConstructor
	@Builder
	
	public static class PatchRequest {
		private Integer managerStaffId;
		private Integer addressId;
	}
	
	@Getter 
	@Setter
	@NoArgsConstructor
	@AllArgsConstructor
	@Builder
	
	public static class Response {
		private Integer storeId;
		private Integer managerStaffId;
		private Integer addressId;
		private LocalDateTime lastUpdate; 
	}
	
}