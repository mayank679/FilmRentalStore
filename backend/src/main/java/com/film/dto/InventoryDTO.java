package com.film.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class InventoryDTO
{
    private Integer inventoryId;

    @NotNull(message = "Film Id is required")
    private Integer filmId;
    @NotNull(message = "Store Id is required")
    private Integer storeId;

    private LocalDateTime lastUpdate;

}