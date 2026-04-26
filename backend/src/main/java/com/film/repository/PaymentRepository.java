package com.film.repository;

import com.film.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface PaymentRepository extends JpaRepository<Payment, Integer>
{
    List<Payment> findByCustomer_CustomerId(Integer customerId);
    List<Payment> findByStaff_StaffId(Integer staffId);
    List<Payment> findByRental_RentalId(Integer rentalId);
    List<Payment> findByPaymentDate(LocalDateTime paymentDate);
    List<Payment> findByAmountGreaterThan(Double amount);
}