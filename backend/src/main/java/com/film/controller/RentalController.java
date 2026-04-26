package com.film.controller;

import com.film.dto.RentalDTO;
import com.film.dto.RentalResponseDTO;
import com.film.dto.ResponseDTO;
import com.film.services.RentalService;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/rental")
public class RentalController {

    private final RentalService service;

    public RentalController(RentalService service) {
        this.service = service;
    }

    @GetMapping("/getAllRental")
    public List<RentalDTO> getAllRentals() {
        return service.getAllRentals();
    }

    @GetMapping("/paged")
    public ResponseEntity<Page<RentalDTO>> getPagedRentals(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(service.findAllPaginated(page, size));
    }

    @GetMapping("/{id}")
    public RentalDTO getRentalById(@PathVariable Integer id) {
        return service.getRentalById(id);
    }

    @GetMapping("/customer/{id}")
    public List<RentalDTO> getByCustomer(@PathVariable Integer id) {
        return service.getByCustomerId(id);
    }

    @GetMapping("/inventory/{id}")
    public List<RentalDTO> getByInventory(@PathVariable Integer id) {
        return service.getByInventoryId(id);
    }

    @GetMapping("/staff/{id}")
    public List<RentalDTO> getByStaff(@PathVariable Integer id) {
        return service.getByStaffId(id);
    }

    @PostMapping
    public RentalDTO createRental(@RequestBody RentalDTO dto) {
        return service.createRental(dto);
    }

    @PutMapping("/{id}")
    public RentalDTO updateRental(@PathVariable Integer id,
                                  @RequestBody RentalDTO dto) {
        return service.updateRental(id, dto);
    }

    /**
     * Search by rental date range.
     * Params renamed from start/end → startDate/endDate to match frontend.
     * Accepts ISO datetime strings e.g. 2005-05-24T22:53:30
     */
    @GetMapping("/date-range")
    public List<RentalDTO> getByDateRange(
            @RequestParam String startDate,
            @RequestParam String endDate) {
        return service.getByRentalDateRange(
                LocalDateTime.parse(startDate),
                LocalDateTime.parse(endDate)
        );
    }

    /**
     * Search by return date range.
     * Accepts ISO datetime strings instead of raw LocalDateTime to avoid binding issues.
     */
    @GetMapping("/return-date-range")
    public List<RentalDTO> getByReturnDateRange(
            @RequestParam String startDate,
            @RequestParam String endDate) {
        return service.getByReturnDateRange(
                LocalDateTime.parse(startDate),
                LocalDateTime.parse(endDate)
        );
    }

    @GetMapping("/last-update-range")
    public List<RentalDTO> getByLastUpdateRange(
            @RequestParam String startDate,
            @RequestParam String endDate) {
        return service.getByLastUpdateRange(
                LocalDateTime.parse(startDate),
                LocalDateTime.parse(endDate)
        );
    }

    /**
     * Rental + Inventory detail view.
     * Requires rentalId and inventoryId as query params.
     */
    @GetMapping("/rental-inventory")
    public ResponseEntity<ResponseDTO<RentalResponseDTO>> getRentalInventory(
            @RequestParam Integer rentalId,
            @RequestParam Integer inventoryId) {
        RentalResponseDTO data = service.getRentalInventory(rentalId, inventoryId);
        return ResponseEntity.ok(new ResponseDTO<>("SUCCESS", "Fetched successfully", data));
    }

    /**
     * Rental + Customer detail view.
     */
    @GetMapping("/rental-customer")
    public ResponseEntity<ResponseDTO<RentalResponseDTO>> getRentalCustomer(
            @RequestParam Integer rentalId,
            @RequestParam Integer customerId) {
        RentalResponseDTO data = service.getRentalCustomer(rentalId, customerId);
        return ResponseEntity.ok(new ResponseDTO<>("SUCCESS", "Rental + Customer fetched successfully", data));
    }

    /**
     * Rental + Staff detail view.
     */
    @GetMapping("/rental-staff")
    public ResponseEntity<ResponseDTO<RentalResponseDTO>> getRentalStaff(
            @RequestParam Integer rentalId,
            @RequestParam Integer staffId) {
        RentalResponseDTO data = service.getRentalStaff(rentalId, staffId);
        return ResponseEntity.ok(new ResponseDTO<>("SUCCESS", "Rental + Staff fetched successfully", data));
    }
}