package com.film.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class CategoryRequestDTO {
    @NotBlank(message = "Category name is required")
    @Size(max=25, message="Category name must be within 25 chars")
    private String name;
}
