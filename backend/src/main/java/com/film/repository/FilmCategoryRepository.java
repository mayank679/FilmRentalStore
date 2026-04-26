package com.film.repository;


import com.film.entity.FilmCategory;
import com.film.entity.FilmCategoryId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface FilmCategoryRepository
        extends JpaRepository<FilmCategory, FilmCategoryId> {

    // All categories assigned to a specific film
    List<FilmCategory> findByFilm_FilmId(Integer filmId);

    // All films assigned to a specific category
    List<FilmCategory> findByCategory_CategoryId(Integer categoryId);

    // All films in a category by category name (case-insensitive)
    @Query("SELECT fc FROM FilmCategory fc " +
           "JOIN fc.category c " +
           "WHERE LOWER(c.name) = LOWER(:categoryName)")
    List<FilmCategory> findByCategoryName(@Param("categoryName") String categoryName);

    // All films in a category that also have a specific rating
    @Query("SELECT fc FROM FilmCategory fc " +
           "JOIN fc.film f " +
           "WHERE fc.category.categoryId = :categoryId " +
           "AND f.rating = :rating")
    List<FilmCategory> findByCategoryIdAndFilmRating(
            @Param("categoryId") Integer categoryId,
            @Param("rating") com.film.entity.Film rating);

    // Check if a (filmId, categoryId) pair already exists
    boolean existsById(FilmCategoryId id);

    // Count films in a category
    long countByCategory_CategoryId(Integer categoryId);

    // Count categories for a film
    long countByFilm_FilmId(Integer filmId);
    
    @Modifying
    @Query("DELETE FROM FilmCategory fc WHERE fc.film.filmId = :filmId")
    void deleteByFilmId(@Param("filmId") Integer filmId);
}