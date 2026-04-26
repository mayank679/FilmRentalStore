package com.film.services;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.film.dto.StoreDTO;
import com.film.entity.Store;
import com.film.exception.StoreManagerAlreadyAssignedException;
import com.film.exception.StoreNotFoundException;
import com.film.repository.StoreRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class StoreService {
	
	private final StoreRepository storeRepository;
	
	//1st one
	public StoreDTO.Response toResponse(Store store) {
		return StoreDTO.Response.builder()
				.storeId(store.getStoreId())
				.managerStaffId(store.getManagerStaffId())
				.addressId(store.getAddressId())
				.lastUpdate(store.getLastUpdate())
				.build();
				
	}
	//2nd one
	private Store findOrThrow(Integer id) {
		
		return storeRepository.findById(id)
				.orElseThrow(() -> new StoreNotFoundException(id));
	}
	
	//GET 
	public StoreDTO.Response getStoreById(Integer id) {
		return toResponse(findOrThrow(id));
	}
	public Page<StoreDTO.Response> findAllPaginated(int page, int size) {
		return storeRepository.findAll(PageRequest.of(page, size))
				.map(this::toResponse);
	}

	public List <StoreDTO.Response> getAllStores() {
		return storeRepository.findAll()
				.stream().map(this::toResponse).collect(Collectors.toList());
	}
	
	//POST
	@Transactional
	public StoreDTO.Response createStore(StoreDTO.Request request) {
		if (storeRepository.existsByManagerStaffId(request.getManagerStaffId())) {
			throw new StoreManagerAlreadyAssignedException(request.getManagerStaffId());
		}
		
		/*if (!staffRepository.existsById(request.getManagerStaffId())) {
	        throw new StaffNotFoundException(request.getManagerStaffId());
	    }*/
		
		Store store = Store.builder()
				.managerStaffId(request.getManagerStaffId())
				.addressId(request.getAddressId())
				.lastUpdate(LocalDateTime.now())
				.build();
		return toResponse(storeRepository.save(store));
	}
	
	//PUT
	@Transactional
	public StoreDTO.Response updateStore(Integer id, StoreDTO.Request request) {
		Store store = findOrThrow(id);
		
		boolean managerChanging = ! store.getManagerStaffId().equals(request.getManagerStaffId());
		if (managerChanging && storeRepository.existsByManagerStaffId(request.getManagerStaffId())) {
			throw new StoreManagerAlreadyAssignedException(request.getManagerStaffId());
		}
		
		store.setManagerStaffId(request.getManagerStaffId());
		store.setAddressId(request.getAddressId());
		store.setLastUpdate(LocalDateTime.now());
		
		return toResponse(storeRepository.save(store));
	}
	
	//PATCH
	public StoreDTO.Response patchStore(Integer id, StoreDTO.PatchRequest patchRequest) {
		Store store = findOrThrow(id);
		
		if (patchRequest.getManagerStaffId() != null
				&& ! store.getManagerStaffId().equals(patchRequest.getManagerStaffId())) {
			if (storeRepository.existsByManagerStaffId(patchRequest.getManagerStaffId())) {
				throw new StoreManagerAlreadyAssignedException(patchRequest.getManagerStaffId());
			}
			store.setManagerStaffId(patchRequest.getManagerStaffId());
		}
		
		if (patchRequest.getAddressId() != null) {
			store.setAddressId(patchRequest.getAddressId());
		}
		
		store.setLastUpdate(LocalDateTime.now());
		return toResponse(storeRepository.save(store));
	}
}

