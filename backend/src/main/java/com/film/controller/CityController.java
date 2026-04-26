package com.film.controller;

import com.film.dto.CityDto;
import com.film.services.CityService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Page;

import java.util.List;

@RestController
@RequestMapping("/api/cities")
public class CityController {

    @Autowired
    private CityService cityService;

    //GET /api/cities
    @GetMapping
    public ResponseEntity<Page<CityDto.Response>> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(cityService.findAllPaginated(page, size));
    }

    //GET /api/cities/{id}
    @GetMapping("/{id}")
    public ResponseEntity<CityDto.Response> getById(@PathVariable Integer id) {
        return ResponseEntity.ok(cityService.findById(id));
    }

    //GET /api/cities/name/{cityName}
    @GetMapping("/name/{cityName}")
    public ResponseEntity<List<CityDto.Response>> getCitiesByName(@PathVariable String cityName) {
        return ResponseEntity.ok(cityService.getCitiesByName(cityName));
    }

    //GET /api/cities/country/id/{countryId}
    // Returns DetailedResponse — includes countryName alongside each city
    @GetMapping("/country/id/{countryId}")
    public ResponseEntity<List<CityDto.DetailedResponse>> getByCountryId(@PathVariable Integer countryId) {
        return ResponseEntity.ok(cityService.findByCountryId(countryId));
    }

    //POST /api/cities
    @PostMapping
    public ResponseEntity<CityDto.Response> create(@Valid @RequestBody CityDto.Request request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(cityService.create(request));
    }

    //PUT /api/cities/{id}
    @PutMapping("/{id}")
    public ResponseEntity<CityDto.Response> update(@PathVariable Integer id,
                                                    @Valid @RequestBody CityDto.Request request) {
        return ResponseEntity.ok(cityService.update(id, request));
    }
}