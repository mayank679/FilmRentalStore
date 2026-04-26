package com.film.controller;


import com.film.dto.InventoryDTO;
import com.film.dto.InventoryResponseDTO;
import com.film.dto.ResponseDTO;
import com.film.services.InventoryService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;


@RestController
@RequestMapping("/api/inventory")
public class InventoryController
{
    private final InventoryService service;

    public InventoryController(InventoryService service) {
        this.service = service;
    }


    // GET all inventory (paginated)
    @GetMapping("/paged")
    public ResponseEntity<Page<InventoryDTO>> getAllInventoryPaged(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(service.findAllPaginated(page, size));
    }

    @GetMapping
    public List<InventoryDTO> getAllInventory() {
        return service.getAllInventory();
    }

    @GetMapping("/{id}")
    public InventoryDTO getInventoryById(@PathVariable Integer id) {
        return service.getInventoryById(id);
    }

    @GetMapping("/film/{filmId}")
    public List<InventoryDTO> getByFilmId(@PathVariable Integer filmId) {
        return service.getByFilmId(filmId);
    }


    @GetMapping("/store/{storeId}")
    public List<InventoryDTO> getByStoreId(@PathVariable Integer storeId) {
        return service.getByStoreId(storeId);
    }

    @GetMapping("/inventory_page")
    public List<InventoryDTO> getPaginatedInventory(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "100") int size) {

        Pageable pageable = PageRequest.of(page, size);
        return service.getInventoryWithPagination(pageable);
    }

    @GetMapping("/last-update")
    public List<InventoryDTO> getByLastUpdate(
            @RequestParam LocalDateTime lastUpdate) {

        return service.getByLastUpdate(lastUpdate);
    }

    @PostMapping
    public InventoryDTO createInventory(@RequestBody InventoryDTO dto) {
        return service.createInventory(dto);
    }


    @PutMapping("/{id}")
    public InventoryDTO updateInventory(@PathVariable Integer id,
                                        @RequestBody InventoryDTO dto) {
        return service.updateInventory(id, dto);
    }

    @GetMapping("/inventory-store")
    public ResponseEntity<ResponseDTO<InventoryResponseDTO>> getInventoryStore(
            @RequestParam Integer inventoryId,
            @RequestParam Integer storeId) {

        InventoryResponseDTO data =
                service.getInventoryStore(inventoryId, storeId);

        return ResponseEntity.ok(
                new ResponseDTO<>("SUCCESS", "Inventory + Store fetched successfully", data)
        );
    }

    @GetMapping("/inventory-film")
    public ResponseEntity<ResponseDTO<InventoryResponseDTO>> getInventoryFilm(
            @RequestParam Integer inventoryId,
            @RequestParam Integer filmId) {

        InventoryResponseDTO data =
                service.getInventoryFilm(inventoryId, filmId);

        return ResponseEntity.ok(
                new ResponseDTO<>("SUCCESS", "Inventory + Film fetched successfully", data)
        );
    }
}
