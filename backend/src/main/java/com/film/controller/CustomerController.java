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
import com.film.services.CustomerService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/customers")
@RequiredArgsConstructor
public class CustomerController {
	
	private final CustomerService customerService;
	
    // GET all customers (paginated)
    @GetMapping("/paged")
    public ResponseEntity<Page<CustomerDTO.Response>> getAllCustomersPaged(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(customerService.findAllPaginated(page, size));
    }

	@GetMapping
	public ResponseEntity<List<CustomerDTO.Response>> getAllCustomers() {
		return ResponseEntity.ok(customerService.getAllCustomers());
	}
	
	@GetMapping("/{id}")
	public ResponseEntity<CustomerDTO.Response> getCustomerById (@PathVariable("id") Integer id) {
		return ResponseEntity.ok(customerService.getCustomerById(id));
	}
	
	@GetMapping("/store/{storeId}")
	public ResponseEntity<List<CustomerDTO.Response>> getCustomerByStore (@PathVariable("storeId") Integer storeId) {
		return ResponseEntity.ok(customerService.getCustomerByStore(storeId));
	}
	
	@PostMapping
	public ResponseEntity<CustomerDTO.Response> createCustomer(@RequestBody CustomerDTO.Request request) {
		return ResponseEntity.status(HttpStatus.CREATED).body(customerService.createCustomer(request));
	}
	
	@PutMapping("/{id}")
	public ResponseEntity<CustomerDTO.Response> updateCustomer(@PathVariable("id") Integer id, 
			@RequestBody CustomerDTO.Request putRequuest) {
		return ResponseEntity.ok(customerService.updateCustomer(id, putRequuest));
	}
}
