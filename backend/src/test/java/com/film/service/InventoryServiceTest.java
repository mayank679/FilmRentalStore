package com.film.service;

import com.film.dto.InventoryDTO;
import com.film.entity.*;
import com.film.exception.InventoryNotFoundException;
import com.film.exception.ResourceNotFoundException;
import com.film.repository.*;
import com.film.services.FilmService;
import com.film.services.InventoryService;
import com.film.services.StoreService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class InventoryServiceTest {

    @Mock
    private InventoryRepository repo;

    @Mock
    private FilmRepository filmRepo;

    @Mock
    private StoreRepository storeRepo;

    @Mock
    private StoreService storeService;

    @Mock
    private FilmService filmService;

    private InventoryService service;

    @BeforeEach
    void setup() {
        service = new InventoryService(repo, filmRepo, storeRepo);

        ReflectionTestUtils.setField(service, "storeService", storeService);
        ReflectionTestUtils.setField(service, "filmService", filmService);
    }

    private Film getFilm(Integer id) {
        Film film = new Film();
        film.setFilmId(id);
        return film;
    }

    private Store getStore(Integer id) {
        Store store = new Store();
        store.setStoreId(id);
        return store;
    }

    private Inventory getInventory(Integer id) {
        Inventory inv = new Inventory();
        inv.setInventoryId(id);
        inv.setFilm(getFilm(1));
        inv.setStore(getStore(1));
        inv.setLastUpdate(LocalDateTime.now());
        return inv;
    }

    @Test
    void testGetInventoryStoreSuccess() {
        Inventory inv = getInventory(1);

        when(repo.findById(1)).thenReturn(Optional.of(inv));
        when(storeService.toResponse(any())).thenReturn(null);

        var result = service.getInventoryStore(1, 1);

        assertThat(result).isNotNull();
    }

    @Test
    void testGetInventoryFilmSuccess() {
        Inventory inv = getInventory(1);

        when(repo.findById(1)).thenReturn(Optional.of(inv));
        when(filmService.toDTO(any())).thenReturn(null);

        var result = service.getInventoryFilm(1, 1);

        assertThat(result).isNotNull();
    }

    @Test
    void testGetInventoryByIdNotFound() {
        when(repo.findById(1)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.getInventoryById(1))
                .isInstanceOf(InventoryNotFoundException.class);
    }

    @Test
    void testCreateInventory() {
        InventoryDTO dto = new InventoryDTO();
        dto.setFilmId(1);
        dto.setStoreId(1);
        dto.setLastUpdate(LocalDateTime.now());

        when(filmRepo.findById(1)).thenReturn(Optional.of(getFilm(1)));
        when(storeRepo.findById(1)).thenReturn(Optional.of(getStore(1)));
        when(repo.save(any())).thenAnswer(i -> i.getArgument(0));

        var result = service.createInventory(dto);

        assertThat(result.getFilmId()).isEqualTo(1);
    }
}