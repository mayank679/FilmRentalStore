package com.film.repository;

import com.film.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository

public interface CustomerRepository extends JpaRepository<Customer, Integer> {

    List<Customer> findByStore_StoreId(Integer storeId);

    Optional<Customer> findByEmail(String email);

    List<Customer> findByActive(Boolean active);

    List<Customer> findByLastNameContainingIgnoreCase(String lastName);

    boolean existsByEmail(String email);
}

