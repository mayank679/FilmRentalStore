package com.film.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.cglib.core.Local;

import java.time.LocalDateTime;

@Data
public class RentalDTO
{
    private Integer rentalId;

    @NotNull(message = "Inventory Id is required")
    private Integer inventoryId;
    @NotNull(message = "Customer Id is required")
    private Integer customerId;
    @NotNull(message = "Staff Id is required")
    private Integer staffId;
    @NotNull(message = "Rental Date is required")
    private LocalDateTime rentalDate;
    private LocalDateTime returnDate;

}
