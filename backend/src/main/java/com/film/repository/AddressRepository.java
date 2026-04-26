package com.film.repository;

import com.film.entity.Address;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

@Repository
public interface AddressRepository extends JpaRepository<Address, Integer> {

	Page<Address> findAll(Pageable pageable);
	
    List<Address> findByCity_CityId(Integer cityId);

    List<Address> findByDistrictIgnoreCase(String district);

    @Query("SELECT a FROM Address a WHERE a.postalCode = :postalCode")
    List<Address> findByPostalCode(String postalCode);
 
    List<Address> findByPhone(String phone);

    @Query("""
            SELECT a FROM Address a
            JOIN FETCH a.city c
            JOIN FETCH c.country co
            WHERE co.countryId = :countryId
            """)
    List<Address> findByCountryId(@Param("countryId") Integer countryId);

    @Query("""
            SELECT a FROM Address a
            JOIN FETCH a.city c
            JOIN FETCH c.country co
            WHERE LOWER(a.address)  LIKE LOWER(CONCAT('%', :term, '%'))
               OR LOWER(c.city)     LIKE LOWER(CONCAT('%', :term, '%'))
               OR LOWER(co.country) LIKE LOWER(CONCAT('%', :term, '%'))
               OR LOWER(a.district) LIKE LOWER(CONCAT('%', :term, '%'))
            """)
    List<Address> searchByTerm(@Param("term") String term);

    @Query("SELECT a FROM Address a WHERE a.location IS NOT NULL")
    List<Address> findAllWithLocation();


    @Query(value = """
    	    SELECT * FROM address
    	    WHERE location IS NOT NULL
    	      AND ST_Distance_Sphere(location, ST_GeomFromText(:point, 4326)) <= :metres
    	    """, nativeQuery = true)
    	List<Address> findWithinRadius(
    	        @Param("point")  String point,
    	        @Param("metres") double metres);
}