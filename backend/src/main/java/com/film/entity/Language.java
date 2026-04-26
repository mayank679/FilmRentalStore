package com.film.entity;

import jakarta.persistence.*;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "language")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Language {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "language_id", columnDefinition = "TINYINT UNSIGNED")
    private Byte languageId;   // 🔥 important

    @Column(name = "name", nullable = false, length = 20)
    private String name;

    @Column(name = "last_update")
    private LocalDateTime lastUpdate;


    // Getters & Setters


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocalDateTime getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(LocalDateTime lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    // Reverse: films using this as primary language
    @OneToMany(mappedBy = "language", fetch = FetchType.LAZY)
    private List<Film> films;

    // Reverse: films using this as original language
    @OneToMany(mappedBy = "originalLanguage", fetch = FetchType.LAZY)
    private List<Film> originalLanguageFilms;

}