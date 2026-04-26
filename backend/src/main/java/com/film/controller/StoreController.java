package com.film.controller;

import org.springframework.data.domain.Page;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.film.dto.CustomerDTO;
import com.film.dto.StoreDTO;
import com.film.services.StoreService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/stores")
@RequiredArgsConstructor
public class StoreController {
	
	private final StoreService storeService;
	
	// GET all stores (paginated)
	@GetMapping("/paged")
	public ResponseEntity<Page<StoreDTO.Response>> getAllStoresPaged(
			@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "10") int size) {
		return ResponseEntity.ok(storeService.findAllPaginated(page, size));
	}

	@GetMapping
	public ResponseEntity<List<StoreDTO.Response>> getAllStores() {
		return ResponseEntity.ok(storeService.getAllStores());
	}
	
	@GetMapping("/{id}")
	public ResponseEntity<StoreDTO.Response> getStoreById (@PathVariable("id") Integer id ) {
		return ResponseEntity.ok(storeService.getStoreById(id));
	}
	
	@PostMapping
	public ResponseEntity<StoreDTO.Response> createStore(@RequestBody StoreDTO.Request request) {
		return ResponseEntity.status(HttpStatus.CREATED).body(storeService.createStore(request));
	}
	
	@PutMapping("/{id}")
	public ResponseEntity<StoreDTO.Response> updateStore (@PathVariable("id") Integer id, 
			@RequestBody StoreDTO.Request putRequest) {
		return ResponseEntity.ok(storeService.updateStore(id, putRequest));
	}
	
}
