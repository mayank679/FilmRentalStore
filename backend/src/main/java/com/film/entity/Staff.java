package com.film.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "staff")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Staff {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "staff_id", columnDefinition = "TINYINT UNSIGNED")
	private Integer staffId;
	

	@Column(name = "first_name", nullable = false, length = 45)
	private String firstName;
	
	@Column(name = "last_name", nullable = false, length = 45)
	private String lastName;
	
	@Column(name = "address_id", nullable = false, columnDefinition = "SMALLINT UNSIGNED")
	private Integer addressId;
	
	@Lob
	@Column(name = "picture", columnDefinition = "BLOB")
	private byte[] picture;
	
	@Column(name = "email", length = 50)
	private String email;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "store_id", nullable = false)
	private Store store;
	
	@Builder.Default
	@Column(name = "active", nullable = false)
	private boolean active = true;
	
	@Column(name = "username", nullable = false, length = 20)
	private String username;
	
	@Column(name = "password", length = 40)
	private String password;
	
	@Column(name = "last_update")
	private LocalDateTime lastUpdate;
	
}