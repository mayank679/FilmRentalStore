package com.film.repository;


import com.film.entity.City;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;
import java.util.Optional;

@Repository
public interface CityRepository extends JpaRepository<City, Integer> {

	Page<City> findAll(Pageable pageable);
	
    List<City> findByCountry_CountryId(Integer countryId);

    Optional<City> findByCityIgnoreCase(String city);

    List<City> findByCityContainingIgnoreCase(String city);

    boolean existsByCityIgnoreCaseAndCountry_CountryId(String city, Integer countryId);

    List<City> findByCountry_CountryIgnoreCase(String country);
    
}


  