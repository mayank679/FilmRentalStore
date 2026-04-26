package com.film.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "country")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor 
@Builder
public class Country {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "country_id", columnDefinition = "SMALLINT UNSIGNED")
    private Integer countryId;

    @Column(name = "country", nullable = false, length = 50, unique = true)
    private String country;

    @Column(name = "last_update", insertable = false, updatable = false)
    private LocalDateTime lastUpdate;
}