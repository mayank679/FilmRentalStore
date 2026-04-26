package com.film.service;

import com.film.dto.StaffDTO;
import com.film.entity.*;
import com.film.exception.StaffNotFoundException;
import com.film.exception.StaffUsernameAlreadyExistsException;
import com.film.exception.StoreNotFoundException;
import com.film.repository.StaffRepository;
import com.film.repository.StoreRepository;
import com.film.services.StaffService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class StaffServiceTest {

    @Mock
    private StaffRepository staffRepository;

    @Mock
    private StoreRepository storeRepository;

    @InjectMocks
    private StaffService service;

    private Staff getStaff(Integer id) {
        Store store = new Store();
        store.setStoreId(1);

        Staff s = new Staff();
        s.setStaffId(id);
        s.setFirstName("John");
        s.setLastName("Doe");
        s.setUsername("john");
        s.setStore(store);
        s.setActive(true);
        return s;
    }

    private StaffDTO.Request getRequest() {
        StaffDTO.Request req = new StaffDTO.Request();
        req.setFirstName("John");
        req.setLastName("Doe");
        req.setUsername("john");
        req.setPassword("pass");
        req.setStoreId(1);
        req.setActive(true);
        return req;
    }

    @Test
    void testGetAllStaff() {
        when(staffRepository.findAll()).thenReturn(List.of(getStaff(1)));

        var result = service.getAllStaff();

        assertThat(result).hasSize(1);
    }

    @Test
    void testGetStaffByIdSuccess() {
        when(staffRepository.findById(1)).thenReturn(Optional.of(getStaff(1)));

        var result = service.getStaffById(1);

        assertThat(result.getStaffId()).isEqualTo(1);
    }

    @Test
    void testGetStaffByIdNotFound() {
        when(staffRepository.findById(1)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.getStaffById(1))
                .isInstanceOf(StaffNotFoundException.class);
    }

    @Test
    void testGetStaffByUsernameSuccess() {
        when(staffRepository.findByUsername("john"))
                .thenReturn(Optional.of(getStaff(1)));

        var result = service.getStaffByUsername("john");

        assertThat(result.getUsername()).isEqualTo("john");
    }

    @Test
    void testCreateStaffSuccess() {
        StaffDTO.Request req = getRequest();

        when(staffRepository.existsByUsername("john")).thenReturn(false);
        when(storeRepository.findById(1)).thenReturn(Optional.of(new Store()));
        when(staffRepository.save(any())).thenAnswer(i -> {
            Staff s = i.getArgument(0);
            s.setStaffId(1);
            return s;
        });

        var result = service.createStaff(req);

        assertThat(result.getStaffId()).isEqualTo(1);
    }

    @Test
    void testCreateStaffDuplicateUsername() {
        StaffDTO.Request req = getRequest();

        when(staffRepository.existsByUsername("john")).thenReturn(true);

        assertThatThrownBy(() -> service.createStaff(req))
                .isInstanceOf(StaffUsernameAlreadyExistsException.class);
    }

    @Test
    void testCreateStaffStoreNotFound() {
        StaffDTO.Request req = getRequest();

        when(staffRepository.existsByUsername("john")).thenReturn(false);
        when(storeRepository.findById(1)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.createStaff(req))
                .isInstanceOf(StoreNotFoundException.class);
    }

    @Test
    void testUpdateStaffSuccess() {
        Staff existing = getStaff(1);
        StaffDTO.Request req = getRequest();

        when(staffRepository.findById(1)).thenReturn(Optional.of(existing));
        when(storeRepository.findById(1)).thenReturn(Optional.of(new Store()));
        when(staffRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        var result = service.updateStaff(1, req);

        assertThat(result.getUsername()).isEqualTo("john");
    }

    @Test
    void testUpdateStaffNotFound() {
        when(staffRepository.findById(1)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.updateStaff(1, getRequest()))
                .isInstanceOf(StaffNotFoundException.class);
    }

    @Test
    void testPatchStaffSuccess() {
        Staff existing = getStaff(1);

        StaffDTO.PatchRequest req = new StaffDTO.PatchRequest();
        req.setFirstName("Updated");

        when(staffRepository.findById(1)).thenReturn(Optional.of(existing));
        when(staffRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        var result = service.patchStaff(1, req);

        assertThat(result.getFirstName()).isEqualTo("Updated");
    }
}