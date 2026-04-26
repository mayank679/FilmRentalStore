package com.film.controller;

import org.springframework.data.domain.Page;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.film.dto.StaffDTO;
import com.film.repository.StaffRepository;
import com.film.services.StaffService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/staff")
@RequiredArgsConstructor
public class StaffController {
	
	private final StaffService staffService;
	
	// GET all staff (paginated)
	@GetMapping("/paged")
	public ResponseEntity<Page<StaffDTO.Response>> getAllStaffPaged(
			@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "10") int size) {
		return ResponseEntity.ok(staffService.findAllPaginated(page, size));
	}

	@GetMapping
	public ResponseEntity <List<StaffDTO.Response>> getAllStaff() {
		return ResponseEntity.ok(staffService.getAllStaff());
	}
	
	@GetMapping("/{id}")
	public ResponseEntity<StaffDTO.Response> getStaffById (@PathVariable("id") Integer id) {
		return ResponseEntity.ok(staffService.getStaffById(id));
	}
	
	@GetMapping("/store/{storeId}")
	public ResponseEntity<List<StaffDTO.Response>> getStaffByStore (@PathVariable("storeId") Integer storeId) {
		return ResponseEntity.ok(staffService.getStaffByStore(storeId));
	}
	
	@GetMapping("/filter")
	public ResponseEntity<List<StaffDTO.Response>> getStaffByActive (@RequestParam Boolean active) {
		return ResponseEntity.ok(staffService.getStaffByActive(active));
	}
	
	@GetMapping("/username/{username}")
	public ResponseEntity<StaffDTO.Response> getStaffByUsername (@PathVariable("username") String username) {
		return ResponseEntity.ok(staffService.getStaffByUsername(username));
	}
	
	@PostMapping
	public ResponseEntity<StaffDTO.Response> createStaff (@RequestBody StaffDTO.Request request) {
		return ResponseEntity.status(HttpStatus.CREATED).body(staffService.createStaff(request));
	}
	
	@PutMapping("/{id}")
	public ResponseEntity<StaffDTO.Response> updateStaff (@PathVariable("id") Integer id,
			@RequestBody StaffDTO.Request putRequest) {
		return ResponseEntity.ok(staffService.updateStaff(id, putRequest));
	}
	
	@PatchMapping("/{id}")
	public ResponseEntity<StaffDTO.Response> patchStaff (@PathVariable("id") Integer id, 
			@RequestBody StaffDTO.PatchRequest patchRequest) {
		return ResponseEntity.ok(staffService.patchStaff(id, patchRequest));
	}
}
