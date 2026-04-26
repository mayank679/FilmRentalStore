package com.film.controller;

import com.film.dto.CountryDto;

import com.film.dto.CountryDto.Response;
import com.film.services.CountryService;
import jakarta.validation.Valid;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/countries")
public class CountryController {

    private final CountryService countryService;

    public CountryController(CountryService countryService) {
        this.countryService = countryService;
    }

    //GET
    @GetMapping
    public ResponseEntity<Page<Response>> getAllCountries(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(countryService.findAllPaginated(page, size));
    }
    
    //GET BY COUNTRY ID
    @GetMapping("/{id}") 
    public ResponseEntity<CountryDto.Response> getCountryById(@PathVariable Integer id) {
        return ResponseEntity.ok(countryService.findById(id));
    }
    
    
    //GET BY COUNTRY NAME 
    @GetMapping("/name/{countryName}")
    public ResponseEntity<List<Response>> getByCountryName(
            @PathVariable String countryName) {

        return ResponseEntity.ok(
                countryService.getCountriesByName(countryName)
        );
    }

    
    

    //POST
    @PostMapping
    public ResponseEntity<CountryDto.Response> createCountry(
            @Valid @RequestBody CountryDto.Request request) {
        return ResponseEntity.ok(countryService.create(request));
    }

    
    
    
    //PUT
    @PutMapping("/{id}")
    public ResponseEntity<CountryDto.Response> updateCountry(
            @PathVariable Integer id,
            @Valid @RequestBody CountryDto.Request request) {
        return ResponseEntity.ok(countryService.update(id, request));
    }

}