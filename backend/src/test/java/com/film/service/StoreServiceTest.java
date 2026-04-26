package com.film.service;

import com.film.dto.StoreDTO;
import com.film.entity.Store;
import com.film.exception.StoreManagerAlreadyAssignedException;
import com.film.exception.StoreNotFoundException;
import com.film.repository.StoreRepository;
import com.film.services.StoreService;
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
class StoreServiceTest {

    @Mock
    private StoreRepository storeRepository;

    @InjectMocks
    private StoreService service;

    private Store getStore(Integer id) {
        Store store = new Store();
        store.setStoreId(id);
        store.setManagerStaffId(1);
        store.setAddressId(1);
        return store;
    }

    private StoreDTO.Request getRequest() {
        StoreDTO.Request req = new StoreDTO.Request();
        req.setManagerStaffId(1);
        req.setAddressId(1);
        return req;
    }

    @Test
    void testGetStoreByIdSuccess() {
        when(storeRepository.findById(1)).thenReturn(Optional.of(getStore(1)));

        var result = service.getStoreById(1);

        assertThat(result.getStoreId()).isEqualTo(1);
    }

    @Test
    void testGetStoreByIdNotFound() {
        when(storeRepository.findById(1)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.getStoreById(1))
                .isInstanceOf(StoreNotFoundException.class);
    }

    @Test
    void testGetAllStores() {
        when(storeRepository.findAll()).thenReturn(List.of(getStore(1)));

        var result = service.getAllStores();

        assertThat(result).hasSize(1);
    }

    @Test
    void testCreateStoreSuccess() {
        StoreDTO.Request req = getRequest();

        when(storeRepository.existsByManagerStaffId(1)).thenReturn(false);
        when(storeRepository.save(any())).thenAnswer(i -> {
            Store s = i.getArgument(0);
            s.setStoreId(1);
            return s;
        });

        var result = service.createStore(req);

        assertThat(result.getStoreId()).isEqualTo(1);
    }

    @Test
    void testCreateStoreDuplicateManager() {
        StoreDTO.Request req = getRequest();

        when(storeRepository.existsByManagerStaffId(1)).thenReturn(true);

        assertThatThrownBy(() -> service.createStore(req))
                .isInstanceOf(StoreManagerAlreadyAssignedException.class);
    }

    @Test
    void testUpdateStoreSuccess() {
        Store existing = getStore(1);
        StoreDTO.Request req = getRequest();

        when(storeRepository.findById(1)).thenReturn(Optional.of(existing));
        when(storeRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        var result = service.updateStore(1, req);

        assertThat(result.getManagerStaffId()).isEqualTo(1);
    }

    @Test
    void testUpdateStoreNotFound() {
        when(storeRepository.findById(1)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.updateStore(1, getRequest()))
                .isInstanceOf(StoreNotFoundException.class);
    }

    @Test
    void testPatchStoreSuccess() {
        Store existing = getStore(1);

        StoreDTO.PatchRequest req = new StoreDTO.PatchRequest();
        req.setAddressId(2);

        when(storeRepository.findById(1)).thenReturn(Optional.of(existing));
        when(storeRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        var result = service.patchStore(1, req);

        assertThat(result.getAddressId()).isEqualTo(2);
    }

    @Test
    void testPatchStoreDuplicateManager() {
        Store existing = getStore(1);

        StoreDTO.PatchRequest req = new StoreDTO.PatchRequest();
        req.setManagerStaffId(2);

        when(storeRepository.findById(1)).thenReturn(Optional.of(existing));
        when(storeRepository.existsByManagerStaffId(2)).thenReturn(true);

        assertThatThrownBy(() -> service.patchStore(1, req))
                .isInstanceOf(StoreManagerAlreadyAssignedException.class);
    }
}