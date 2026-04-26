package com.film.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.film.entity.Staff;

public interface StaffRepository extends JpaRepository<Staff, Integer>{
	
	List<Staff> findByStore_StoreId (Integer storeId);
	List<Staff> findByActive (Boolean active);
	
	Optional<Staff> findByUsername (String username);
	Optional<Staff> findByEmail (String email);
	
	boolean existsByUsername (String username);
	
}
