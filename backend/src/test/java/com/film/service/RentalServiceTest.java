package com.film.service;

import com.film.dto.RentalDTO;
import com.film.entity.*;
import com.film.exception.RentalNotFoundException;
import com.film.exception.ResourceNotFoundException;
import com.film.repository.*;
import com.film.services.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class RentalServiceTest {

    @Mock private RentalRepository repo;
    @Mock private InventoryRepository inventoryRepository;
    @Mock private CustomerRepository customerRepository;
    @Mock private StaffRepository staffRepository;

    @Mock private InventoryService inventoryService;
    @Mock private CustomerService customerService;
    @Mock private StaffService staffService;

    private RentalService service;

    @BeforeEach
    void setup() {
        service = new RentalService(repo);

        ReflectionTestUtils.setField(service, "inventoryRepository", inventoryRepository);
        ReflectionTestUtils.setField(service, "customerRepository", customerRepository);
        ReflectionTestUtils.setField(service, "staffRepository", staffRepository);
        ReflectionTestUtils.setField(service, "inventoryService", inventoryService);
        ReflectionTestUtils.setField(service, "customerService", customerService);
        ReflectionTestUtils.setField(service, "staffService", staffService);
    }

    private Rental getRental(Integer id) {
        Rental r = new Rental();
        r.setRentalId(id);

        Inventory inv = new Inventory();
        inv.setInventoryId(1);
        r.setInventory(inv);

        Customer c = new Customer();
        c.setCustomerId(1);
        r.setCustomer(c);

        Staff s = new Staff();
        s.setStaffId(1);
        r.setStaff(s);

        r.setRentalDate(LocalDateTime.now());
        return r;
    }

    @Test
    void testGetAllRentals() {
        when(repo.findAll()).thenReturn(List.of(getRental(1)));

        var result = service.getAllRentals();

        assertThat(result).hasSize(1);
    }

    @Test
    void testGetRentalByIdSuccess() {
        when(repo.findById(1)).thenReturn(Optional.of(getRental(1)));

        var result = service.getRentalById(1);

        assertThat(result.getRentalId()).isEqualTo(1);
    }

    @Test
    void testGetRentalByIdNotFound() {
        when(repo.findById(1)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.getRentalById(1))
                .isInstanceOf(RentalNotFoundException.class);
    }

    @Test
    void testGetRentalsWithPagination() {
        when(repo.findAll(PageRequest.of(0, 5)))
                .thenReturn(new PageImpl<>(List.of(getRental(1))));

        var result = service.getRentalsWithPagination(0, 5);

        assertThat(result).hasSize(1);
    }

    @Test
    void testCreateRental() {
        RentalDTO dto = new RentalDTO();
        dto.setInventoryId(1);
        dto.setCustomerId(1);
        dto.setStaffId(1);
        dto.setRentalDate(LocalDateTime.now());

        when(inventoryRepository.findById(1)).thenReturn(Optional.of(new Inventory()));
        when(customerRepository.findById(1)).thenReturn(Optional.of(new Customer()));
        when(staffRepository.findById(1)).thenReturn(Optional.of(new Staff()));
        when(repo.save(any())).thenAnswer(i -> {
            Rental r = i.getArgument(0);
            r.setRentalId(1);
            return r;
        });

        var result = service.createRental(dto);

        assertThat(result.getRentalId()).isEqualTo(1);
    }

    @Test
    void testUpdateRentalSuccess() {
        Rental existing = getRental(1);

        RentalDTO dto = new RentalDTO();
        dto.setInventoryId(1);
        dto.setCustomerId(1);
        dto.setStaffId(1);
        dto.setRentalDate(LocalDateTime.now());

        when(repo.findById(1)).thenReturn(Optional.of(existing));
        when(inventoryRepository.findById(1)).thenReturn(Optional.of(new Inventory()));
        when(customerRepository.findById(1)).thenReturn(Optional.of(new Customer()));
        when(staffRepository.findById(1)).thenReturn(Optional.of(new Staff()));
        when(repo.save(any())).thenAnswer(i -> i.getArgument(0));

        var result = service.updateRental(1, dto);

        assertThat(result.getRentalId()).isEqualTo(1);
    }

    @Test
    void testGetRentalInventorySuccess() {
        Rental r = getRental(1);

        when(repo.findById(1)).thenReturn(Optional.of(r));
        when(inventoryService.convertToDTO(any())).thenReturn(null);

        var result = service.getRentalInventory(1, 1);

        assertThat(result).isNotNull();
    }

    @Test
    void testGetRentalInventoryMismatch() {
        Rental r = getRental(1);

        when(repo.findById(1)).thenReturn(Optional.of(r));

        assertThatThrownBy(() -> service.getRentalInventory(1, 2))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void testGetRentalCustomerSuccess() {
        Rental r = getRental(1);

        when(repo.findById(1)).thenReturn(Optional.of(r));
        when(customerService.toResponse(any())).thenReturn(null);

        var result = service.getRentalCustomer(1, 1);

        assertThat(result).isNotNull();
    }

    @Test
    void testGetRentalStaffSuccess() {
        Rental r = getRental(1);

        when(repo.findById(1)).thenReturn(Optional.of(r));
        when(staffService.toResponse(any())).thenReturn(null);

        var result = service.getRentalStaff(1, 1);

        assertThat(result).isNotNull();
    }
}