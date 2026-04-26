package com.film.repository;

import com.film.entity.Language;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LanguageRepository extends JpaRepository<Language, Integer> {
    boolean existsByName(String name);
}