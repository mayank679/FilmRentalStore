package com.film.repository;

import com.film.entity.Rental;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface RentalRepository extends JpaRepository<Rental,Integer>
{
    List<Rental> findByCustomer_CustomerId(Integer customerId);
    List<Rental> findByInventory_InventoryId(Integer inventoryId);
    List<Rental> findByStaff_StaffId(Integer staffId);
    List<Rental> findByRentalDateBetween(LocalDateTime start, LocalDateTime end);
    List<Rental> findByReturnDateBetween(LocalDateTime start, LocalDateTime end);
    List<Rental> findByLastUpdateBetween(LocalDateTime start, LocalDateTime end);
    @Query("SELECT r FROM Rental r WHERE r.rentalId = :rentalId AND r.inventory.inventoryId = :inventoryId")
    Optional<Rental> findRentalByRentalIdAndInventory(
            @Param("rentalId") Integer rentalId,
            @Param("inventoryId") Integer inventoryId
    );
    @Query("SELECT r FROM Rental r WHERE r.rentalId = :rentalId AND r.customer.customerId = :customerId")
    Optional<Rental> findRentalByRentalIdAndCustomer(
            @Param("rentalId") Integer rentalId,
            @Param("customerId") Integer customerId
    );
}
