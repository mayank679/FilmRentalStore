package com.film.repository;

import com.film.entity.Country;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CountryRepository extends JpaRepository<Country, Integer> {

	Page<Country> findAll(Pageable pageable);
	
    Optional<Country> findByCountryIgnoreCase(String country);

    boolean existsByCountryIgnoreCase(String country);

	List<Country> findByCountryContainingIgnoreCase(String name);
    
}
