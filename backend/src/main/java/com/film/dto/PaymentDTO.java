package com.film.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class PaymentDTO
{
    private Integer paymentId;

    @NotNull(message = "Customer ID is required")
    private Integer customerId;

    @NotNull(message = "Staff ID is required")
    private Integer staffId;

    @NotNull(message = "Rental ID is required")
    private Integer rentalId;

    @NotNull(message = "Amount is required")
    @Positive(message = "Amount must be greater than 0")
    private BigDecimal amount;

    @NotNull(message = "Payment date is required")
    private LocalDateTime paymentDate;

    private LocalDateTime lastUpdate;
}
