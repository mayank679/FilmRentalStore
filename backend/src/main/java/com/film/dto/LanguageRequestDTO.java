package com.film.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class LanguageRequestDTO {
    @NotBlank(message = "Name of language should be present")
    private String name;
}
