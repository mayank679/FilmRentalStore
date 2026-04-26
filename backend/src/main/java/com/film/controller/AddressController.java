package com.film.controller;

import com.film.dto.AddressDto;
import com.film.services.AddressService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Page;

import java.util.List;

@RestController
@RequestMapping("/api/addresses")
public class AddressController {

    @Autowired
    private AddressService addressService;

    //GET /api/addresses
    @GetMapping
    public ResponseEntity<Page<AddressDto.Response>> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(addressService.findAllPaginated(page, size));
    }

    //GET /api/addresses/{id}
    @GetMapping("/{id}")
    public ResponseEntity<AddressDto.Response> getById(@PathVariable Integer id) {
        return ResponseEntity.ok(addressService.findById(id));
    }

    //GET /api/addresses/city/{cityId}
    // Returns DetailedResponse — includes cityName + countryId + countryName
    @GetMapping("/city/{cityId}")
    public ResponseEntity<List<AddressDto.DetailedResponse>> getByCityId(@PathVariable Integer cityId) {
        return ResponseEntity.ok(addressService.findByCityId(cityId));
    }

    //GET /api/addresses/district/{district}
    @GetMapping("/district/{district}")
    public ResponseEntity<List<AddressDto.Response>> getByDistrict(@PathVariable String district) {
        return ResponseEntity.ok(addressService.findByDistrict(district));
    }

    //GET /api/addresses/postal/{postalCode}
    @GetMapping("/postal/{postalCode}")
    public ResponseEntity<List<AddressDto.Response>> getByPostalCode(@PathVariable String postalCode) {
        return ResponseEntity.ok(addressService.findByPostalCode(postalCode));
    }

    //GET /api/addresses/phone/{phone}
    @GetMapping("/phone/{phone}")
    public ResponseEntity<List<AddressDto.Response>> getByPhone(@PathVariable String phone) {
        return ResponseEntity.ok(addressService.findByPhone(phone));
    }

    //GET /api/addresses/location
    @GetMapping("/location")
    public ResponseEntity<List<AddressDto.Response>> getWithLocation() {
        return ResponseEntity.ok(addressService.findAllWithLocation());
    }

    //POST /api/addresses
    @PostMapping
    public ResponseEntity<AddressDto.Response> create(@Valid @RequestBody AddressDto.Request request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(addressService.create(request));
    }

    //PUT /api/addresses/{id}
    @PutMapping("/{id}")
    public ResponseEntity<AddressDto.Response> update(@PathVariable Integer id,
                                                       @Valid @RequestBody AddressDto.Request request) {
        return ResponseEntity.ok(addressService.update(id, request));
    }
}