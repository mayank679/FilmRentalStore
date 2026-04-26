package com.film.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "store")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Store {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "store_id", columnDefinition = "TINYINT UNSIGNED")
	private Integer storeId;
	
	@Column(name = "manager_staff_id", nullable = false, columnDefinition = "TINYINT UNSIGNED")
	private Integer managerStaffId;
	
	@Column(name = "address_id", nullable = false, columnDefinition = "SMALLINT UNSIGNED")
	private Integer addressId;
	
	@Column(name = "last_update")
	private LocalDateTime lastUpdate;
}