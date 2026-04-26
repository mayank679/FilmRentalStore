package com.film.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Customer {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "customer_id", columnDefinition = "SMALLINT UNSIGNED")
	private Integer customerId;
	
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "store_id", nullable = false)
	private Store store;

    @Column(name = "first_name", nullable = false, length = 45)
    private String firstName;
 
    @Column(name = "last_name", nullable = false, length = 45)
    private String lastName;
 
    @Column(name = "email", length = 50)
    private String email;
 
    @Column(name = "address_id", nullable = false, columnDefinition = "SMALLINT UNSIGNED")
    private Integer addressId;
 
    @Column(name = "active", nullable = false)
    private Boolean active = true;
 
    @Column(name = "create_date", nullable = false)
    private LocalDateTime createDate;
 
    @Column(name = "last_update")
    private LocalDateTime lastUpdate;
	
}