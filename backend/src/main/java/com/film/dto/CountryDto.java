package com.film.dto;

import java.time.LocalDateTime;

import jakarta.validation.constraints.*;
import lombok.*;

public class CountryDto {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Request {

        @NotBlank(message = "Country name must not be blank")
        @Size(max = 50, message = "Country name must not exceed 50 characters")
        private String country;

    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder

    public static class PatchRequest {

        @Size(max = 50, message = "Country name must not exceed 50 characters")
        private String country;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Response {

        private Integer countryId;
        private String country;
        private LocalDateTime lastUpdate;
    }
}