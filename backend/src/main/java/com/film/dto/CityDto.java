package com.film.dto;

import java.time.LocalDateTime;

import jakarta.validation.constraints.*;
import lombok.*;

public class CityDto {

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Request {

        @NotBlank(message = "City name must not be blank")
        @Size(max = 50, message = "City name must not exceed 50 characters")
        private String city;

        @NotNull(message = "Country ID must not be null")
        private Integer countryId;
    }

    // Used for: GET all, GET by ID, GET by name, POST, PUT
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Response {

        private Integer cityId;
        private String  city;
        private Integer countryId;
        private LocalDateTime lastUpdate;
    }

    // Used ONLY for: GET /cities/country/id/{countryId}
    // Returns city + full parent country details
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DetailedResponse {

        private Integer cityId;
        private String  city;
        private Integer countryId;
        private String  countryName;   // from parent Country entity
        private LocalDateTime lastUpdate;
    }
}