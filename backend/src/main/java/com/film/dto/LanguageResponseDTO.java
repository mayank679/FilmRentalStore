package com.film.dto;

import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class LanguageResponseDTO {
    private Byte languageId;
    private String name;
}
