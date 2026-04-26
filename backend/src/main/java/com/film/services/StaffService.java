package com.film.services;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.film.dto.StaffDTO;
import com.film.entity.Staff;
import com.film.entity.Store;
import com.film.exception.StaffNotFoundException;
import com.film.exception.StaffUsernameAlreadyExistsException;
import com.film.exception.StoreNotFoundException;
import com.film.repository.StaffRepository;
import com.film.repository.StoreRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class StaffService {
	
	private final StaffRepository staffRepository;
	private final StoreRepository storeRepository;
	
	//1st one
	public StaffDTO.Response toResponse (Staff staff) {
		
		return StaffDTO.Response.builder()
				.staffId(staff.getStaffId())
				.firstName(staff.getFirstName())
				.lastName(staff.getLastName())
				.addressId(staff.getAddressId())
				.email(staff.getEmail())
				.storeId(staff.getStore() != null ? staff.getStore().getStoreId() : null)
				.active(staff.isActive())
				.username(staff.getUsername())
				.lastUpdate(staff.getLastUpdate())
				.build();
		
	}
	
	//2nd one 
	private Staff findOrThrow (Integer id) {
		return staffRepository.findById(id)
				.orElseThrow(() -> new StaffNotFoundException(id));
		
	}
	
	//GET
	public Page<StaffDTO.Response> findAllPaginated(int page, int size) {
		return staffRepository.findAll(PageRequest.of(page, size))
				.map(this::toResponse);
	}

	public List<StaffDTO.Response> getAllStaff () {
		return staffRepository.findAll()
				.stream().map(this::toResponse).collect(Collectors.toList());
	}
	
	public StaffDTO.Response getStaffById(Integer id) {
		return toResponse(findOrThrow(id));
	}

	public List<StaffDTO.Response> getStaffByStore(Integer storeId) {
		return staffRepository.findByStore_StoreId(storeId).stream().map(this::toResponse).collect(Collectors.toList());
	}

	
	public List<StaffDTO.Response> getStaffByActive(Boolean active) {
		return staffRepository.findByActive(active).stream().map(this::toResponse).collect(Collectors.toList());
	}
	
	public StaffDTO.Response getStaffByUsername (String username) {
		Staff staff = staffRepository.findByUsername(username)
				.orElseThrow(() -> new StaffNotFoundException(username));
		return toResponse(staff);
	}
	
	//POST
	@Transactional
    public StaffDTO.Response createStaff(StaffDTO.Request request) {
        if (staffRepository.existsByUsername(request.getUsername())) {
            throw new StaffUsernameAlreadyExistsException(request.getUsername());
        }
 
        Store store = storeRepository.findById(request.getStoreId())
                .orElseThrow(() -> new StoreNotFoundException(request.getStoreId()));
 
        Staff staff = Staff.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .addressId(request.getAddressId())
                .email(request.getEmail())
                .store(store)
                .active(request.getActive() != null ? request.getActive() : true)
                .username(request.getUsername())
                .password(request.getPassword())
                .lastUpdate(LocalDateTime.now())
                .build();
 
        return toResponse(staffRepository.save(staff));
    }
	
	//PUT
	@Transactional
    public StaffDTO.Response updateStaff(Integer id, StaffDTO.Request request) {
        Staff staff = findOrThrow(id);
 
        boolean usernameChanging = !staff.getUsername().equals(request.getUsername());
        if (usernameChanging && staffRepository.existsByUsername(request.getUsername())) {
            throw new StaffUsernameAlreadyExistsException(request.getUsername());
        }
 
        Store store = storeRepository.findById(request.getStoreId())
                .orElseThrow(() -> new StoreNotFoundException(request.getStoreId()));
 
        staff.setFirstName(request.getFirstName());
        staff.setLastName(request.getLastName());
        staff.setAddressId(request.getAddressId());
        staff.setEmail(request.getEmail());
        staff.setStore(store);
        staff.setActive(request.getActive());
        staff.setUsername(request.getUsername());
        staff.setPassword(request.getPassword());
        staff.setLastUpdate(LocalDateTime.now());
 
        return toResponse(staffRepository.save(staff));
    }
	
	//PATCH
	@Transactional
    public StaffDTO.Response patchStaff(Integer id, StaffDTO.PatchRequest request) {
        Staff staff = findOrThrow(id);
 
        if (request.getFirstName() != null)  staff.setFirstName(request.getFirstName());
        if (request.getLastName() != null)   staff.setLastName(request.getLastName());
        if (request.getEmail() != null)      staff.setEmail(request.getEmail());
        if (request.getActive() != null)     staff.setActive(request.getActive());
        if (request.getAddressId() != null)  staff.setAddressId(request.getAddressId());
        if (request.getPassword() != null)   staff.setPassword(request.getPassword());
 
        if (request.getUsername() != null && !staff.getUsername().equals(request.getUsername())) {
            if (staffRepository.existsByUsername(request.getUsername())) {
                throw new StaffUsernameAlreadyExistsException(request.getUsername());
            }
            staff.setUsername(request.getUsername());
        }
 
        if (request.getStoreId() != null) {
            Store store = storeRepository.findById(request.getStoreId())
                    .orElseThrow(() -> new StoreNotFoundException(request.getStoreId()));
            staff.setStore(store);
        }
 
        staff.setLastUpdate(LocalDateTime.now());
        return toResponse(staffRepository.save(staff));
    }
}
