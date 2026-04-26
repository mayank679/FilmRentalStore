package com.film.repository;

import com.film.entity.Inventory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface InventoryRepository extends JpaRepository <Inventory,Integer>
{
    List<Inventory> findByFilm_FilmId(Integer filmId);
    List<Inventory> findByStore_StoreId(Integer storeId);
    Page<Inventory> findAll(Pageable pageable);
    List<Inventory> findByLastUpdate(LocalDateTime lastUpdate);
}
