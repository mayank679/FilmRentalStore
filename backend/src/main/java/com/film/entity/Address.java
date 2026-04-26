package com.film.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

import org.locationtech.jts.geom.Point;


@Entity
@Table(name = "address")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Address {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "address_id", columnDefinition = "SMALLINT UNSIGNED")
	private Integer addressId;


	@Column(name = "address", nullable = false, length = 50)
	private String address;


	@Column(name = "district", nullable = false, length = 20)
	private String district;

	// FK: address.city_id → city.city_id  (EAGER so city+country are always loaded)
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "city_id", nullable = false, foreignKey = @ForeignKey(name = "fk_address_city"))
	private City city;

	@Column(name = "postal_code", length = 10)
	private String postalCode;

	@Column(name = "phone", nullable = false, length = 20)
	private String phone;


	@Column(name = "location", columnDefinition = "POINT")
	private Point location;

	@Column(name = "last_update", insertable = false, updatable = false)
	private LocalDateTime lastUpdate;


}