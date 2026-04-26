package com.film.dto;

import java.time.LocalDateTime;

import jakarta.validation.constraints.*;
import lombok.*;

public class AddressDto {

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Request {

        @NotBlank(message = "Address must not be blank")
        @Size(max = 50, message = "Address must not exceed 50 characters")
        private String address;

        @Size(max = 50, message = "Address2 must not exceed 50 characters")
        private String address2;

        @NotBlank(message = "District must not be blank")
        @Size(max = 20, message = "District must not exceed 20 characters")
        private String district;

        @NotNull(message = "City ID must not be null")
        private Integer cityId;

        @Size(max = 10, message = "Postal code must not exceed 10 characters")
        private String postalCode;

        @NotBlank(message = "Phone must not be blank")
        @Size(max = 20, message = "Phone must not exceed 20 characters")
        private String phone;

        // WKT format: "POINT(longitude latitude)"  e.g. "POINT(88.3639 22.5726)"
        @Pattern( regexp = "^POINT\\((-?\\d+(\\.\\d+)?) (-?\\d+(\\.\\d+)?)\\)$",
                message = "Location must be in format: POINT(longitude latitude)"
        )
        private String location;
    }

    // Used for: GET all, GET by ID, GET by district/phone/postal, POST, PUT
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Response {

        private Integer addressId;
        private String  address;
        private String  district;
        private Integer cityId;
        private String  postalCode;
        private String  phone;
        private String  location;
        private LocalDateTime lastUpdate;
    }

    // Used ONLY for: GET /addresses/city/{cityId}
    // Returns address + full parent city + grandparent country details
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DetailedResponse {

        private Integer addressId;
        private String  address;
        private String  district;
        private Integer cityId;
        private String  cityName;      // from parent City entity
        private Integer countryId;     // from City's parent Country
        private String  countryName;   // from City's parent Country
        private String  postalCode;
        private String  phone;
        private String  location;
        private LocalDateTime lastUpdate;
    }
}