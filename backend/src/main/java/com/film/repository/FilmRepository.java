package com.film.repository;

import com.film.dto.CategoryCountDTO;
import com.film.entity.Film;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface FilmRepository extends JpaRepository<Film, Integer> {



    List<Film> findByLanguage_Name(String name);

    List<Film> findByRating(String rating);

    List<Film> findByTitle(String title);

    List<Film> findByReleaseYear(int releaseYear);

    List<Film> findByLanguage_LanguageId(Integer languageId);

    List<Film> findByRentalDuration(int rentalDuration);

    List<Film> findByRentalRate(BigDecimal rentalRate);

    List<Film> findByLength(Integer length);

    List<Film> findByReplacementCost(BigDecimal replacementCost);

    List<Film> findBySpecialFeatures(String specialFeatures);

    @Query(value = "SELECT c.name FROM category c " +
            "JOIN film_category fc ON c.category_id = fc.category_id " +
            "WHERE fc.film_id = :filmId",
            nativeQuery = true)
    List<String> findCategoryNamesByFilmId(@Param("filmId") Integer filmId);


    @Query("SELECT new com.film.dto.CategoryCountDTO(c.name, COUNT(DISTINCT f.filmId)) " +
            "FROM Film f " +
            "JOIN f.filmCategories fc " +
            "JOIN fc.category c " +
            "GROUP BY c.name")
    List<CategoryCountDTO> countFilmsByCategory();

    @Query("SELECT f.rating, COUNT(f) FROM Film f GROUP BY f.rating")
    List<Object[]> countByRating();

    @Query("SELECT f.releaseYear, COUNT(f) FROM Film f GROUP BY f.releaseYear")
    List<Object[]> countByReleaseYear();

    @Query("SELECT AVG(f.rentalRate) FROM Film f")
    BigDecimal avgRentalRate();

    @Query("SELECT AVG(f.length) FROM Film f")
    Double avgLength();

    @Query("SELECT SUM(f.replacementCost) FROM Film f")
    BigDecimal totalReplacementCost();

    @Query("SELECT SUM(f.rentalDuration) FROM Film f")
    Integer totalRentalDuration();

//~~~~~~~~~~~~~~~~~~~~~~~~~~`
    @Query("SELECT MAX(f.rentalRate) FROM Film f")
    BigDecimal findMaxRentalRate();

    @Query("SELECT MIN(f.rentalRate) FROM Film f")
    BigDecimal findMinRentalRate();

    // 🔹 MAX / MIN replacement cost
    @Query("SELECT MAX(f.replacementCost) FROM Film f")
    BigDecimal findMaxReplacementCost();

    @Query("SELECT MIN(f.replacementCost) FROM Film f")
    BigDecimal findMinReplacementCost();

    // 🔹 RANGE queries
    List<Film> findByRentalRateBetween(BigDecimal min, BigDecimal max);

    List<Film> findByReplacementCostBetween(BigDecimal min, BigDecimal max);

//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    List<Film> findByFilmIdAndRating(Integer filmId, String rating);

    List<Film> findByRatingAndRentalRate(String rating, BigDecimal rentalRate);

    List<Film> findByLengthAndReleaseYear(Integer length, Integer releaseYear);
}