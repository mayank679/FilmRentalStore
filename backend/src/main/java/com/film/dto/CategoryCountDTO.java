package com.film.dto;

import lombok.*;

@Getter @Setter @AllArgsConstructor @NoArgsConstructor
public class CategoryCountDTO {

    private String categoryName;
    private Long filmCount;
}