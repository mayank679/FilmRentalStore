package com.film.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "city")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class City {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "city_id", columnDefinition = "SMALLINT UNSIGNED")
    private Integer cityId;

    @Column(name = "city", nullable = false, length = 50)
    private String city;

    // FK: city.country_id → country.country_id
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "country_id", nullable = false,
            columnDefinition = "SMALLINT UNSIGNED",
            foreignKey = @ForeignKey(name = "fk_city_country"))
    private Country country;

    @Column(name = "last_update", insertable = false, updatable = false)
    private LocalDateTime lastUpdate;
}