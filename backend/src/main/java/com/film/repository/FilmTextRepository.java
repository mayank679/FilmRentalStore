package com.film.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.film.entity.FilmText;

@Repository
public interface FilmTextRepository extends JpaRepository<FilmText, Integer>{


    // Search by title — partial, case-insensitive
    List<FilmText> findByTitleContainingIgnoreCase(String title);
 
    // Search by description — partial, case-insensitive
    List<FilmText> findByDescriptionContainingIgnoreCase(String description);
 
    // Search by keyword in BOTH title and description
    @Query("SELECT ft FROM FilmText ft WHERE " +
           "LOWER(ft.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(ft.description) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<FilmText> searchByKeyword(@Param("keyword") String keyword);
 
    // Get FilmText + full Film details by filmId (JOIN FETCH to avoid lazy load)
    @Query("SELECT ft FROM FilmText ft " +
           "JOIN FETCH ft.film f " +
           "JOIN FETCH f.language " +
           "WHERE ft.filmId = :filmId")
    Optional<FilmText> findByFilmIdWithDetails(@Param("filmId") Integer filmId);

	boolean existsByFilmId(Integer filmId);

}
