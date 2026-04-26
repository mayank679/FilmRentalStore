package com.film.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.film.entity.Store;

@Repository
public interface StoreRepository extends JpaRepository<Store, Integer>{
	
	Optional<Store> findByManagerStaffId (Integer managerStaffId);
	boolean existsByManagerStaffId (Integer managerStaffId);
}