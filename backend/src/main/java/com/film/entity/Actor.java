package com.film.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "actor")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Actor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "actor_id", columnDefinition = "SMALLINT UNSIGNED")
    private Integer actorId;

    @Column(name = "first_name", nullable = false, length = 45)
    private String firstName;

    @Column(name = "last_name", nullable = false, length = 45)
    private String lastName;

    @Column(name = "last_update")
    private LocalDateTime lastUpdate;


    // Reverse: all FilmActor join records for this actor
    @JsonIgnore
    @OneToMany(mappedBy = "actor", fetch = FetchType.LAZY)
    private List<FilmActor> filmActors;

}