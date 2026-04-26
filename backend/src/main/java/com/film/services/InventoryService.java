package com.film.services;

import com.film.dto.FilmDTO;
import com.film.dto.InventoryDTO;
import com.film.dto.InventoryResponseDTO;
import com.film.dto.StoreDTO;
import com.film.entity.Film;
import com.film.entity.Inventory;
import com.film.entity.Store;
import com.film.exception.InventoryNotFoundException;
import com.film.exception.ResourceNotFoundException;
import com.film.repository.FilmRepository;
import com.film.repository.InventoryRepository;
import com.film.repository.StoreRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class InventoryService {

    private final InventoryRepository repo;
    private final FilmRepository filmRepo;
    private final StoreRepository storeRepo;

    @Autowired
    private StoreService storeService;

    @Autowired
    private FilmService filmService;

    public InventoryService(InventoryRepository repo,
                            FilmRepository filmRepo,
                            StoreRepository storeRepo) {
        this.repo = repo;
        this.filmRepo = filmRepo;
        this.storeRepo = storeRepo;
    }

    public InventoryDTO convertToDTO(Inventory inv) {
        InventoryDTO dto = new InventoryDTO();
        dto.setInventoryId(inv.getInventoryId());
        if (inv.getFilm() != null)  dto.setFilmId(inv.getFilm().getFilmId());
        if (inv.getStore() != null) dto.setStoreId(inv.getStore().getStoreId());
        dto.setLastUpdate(inv.getLastUpdate());
        return dto;
    }

    private Inventory convertToEntity(InventoryDTO dto) {
        Inventory inv = new Inventory();
        if (dto.getFilmId() != null) {
            Film film = filmRepo.findById(dto.getFilmId())
                    .orElseThrow(() -> new ResourceNotFoundException("Film", "id", dto.getFilmId()));
            inv.setFilm(film);
        }
        if (dto.getStoreId() != null) {
            Store store = storeRepo.findById(dto.getStoreId())
                    .orElseThrow(() -> new ResourceNotFoundException("Store", "id", dto.getStoreId()));
            inv.setStore(store);
        }
        inv.setLastUpdate(dto.getLastUpdate());
        return inv;
    }

    // GET all (paginated) — returns proper Page<InventoryDTO>
    public Page<InventoryDTO> findAllPaginated(int page, int size) {
        return repo.findAll(PageRequest.of(page, size))
                .map(this::convertToDTO);
    }

    public Inventory save(Inventory inventory) {
        return repo.save(inventory);
    }

    public List<InventoryDTO> getAllInventory() {
        return repo.findAll().stream().map(this::convertToDTO).toList();
    }

    public InventoryDTO getInventoryById(Integer id) {
        Inventory inv = repo.findById(id)
                .orElseThrow(() -> new InventoryNotFoundException(id));
        return convertToDTO(inv);
    }

    public List<InventoryDTO> getByFilmId(Integer filmId) {
        return repo.findByFilm_FilmId(filmId).stream().map(this::convertToDTO).toList();
    }

    public List<InventoryDTO> getByStoreId(Integer storeId) {
        return repo.findByStore_StoreId(storeId).stream().map(this::convertToDTO).toList();
    }

    public List<InventoryDTO> getInventoryWithPagination(Pageable pageable) {
        return repo.findAll(pageable).getContent().stream().map(this::convertToDTO).toList();
    }

    public List<InventoryDTO> getByLastUpdate(LocalDateTime lastUpdate) {
        return repo.findByLastUpdate(lastUpdate).stream().map(this::convertToDTO).toList();
    }

    public InventoryDTO createInventory(InventoryDTO dto) {
        return convertToDTO(repo.save(convertToEntity(dto)));
    }

    public InventoryDTO updateInventory(Integer id, InventoryDTO dto) {
        Inventory existing = repo.findById(id)
                .orElseThrow(() -> new InventoryNotFoundException(id));
        if (dto.getFilmId() != null) {
            Film film = filmRepo.findById(dto.getFilmId())
                    .orElseThrow(() -> new ResourceNotFoundException("Film", "id", dto.getFilmId()));
            existing.setFilm(film);
        }
        if (dto.getStoreId() != null) {
            Store store = storeRepo.findById(dto.getStoreId())
                    .orElseThrow(() -> new ResourceNotFoundException("Store", "id", dto.getStoreId()));
            existing.setStore(store);
        }
        existing.setLastUpdate(dto.getLastUpdate());
        return convertToDTO(repo.save(existing));
    }

    public InventoryResponseDTO getInventoryStore(Integer inventoryId, Integer storeId) {
        Inventory inventory = repo.findById(inventoryId)
                .orElseThrow(() -> new InventoryNotFoundException(inventoryId));
        if (inventory.getStore() == null || !inventory.getStore().getStoreId().equals(storeId)) {
            throw new ResourceNotFoundException("Inventory", "storeId", storeId);
        }
        return new InventoryResponseDTO(convertToDTO(inventory), storeService.toResponse(inventory.getStore()));
    }

    public InventoryResponseDTO getInventoryFilm(Integer inventoryId, Integer filmId) {
        Inventory inventory = repo.findById(inventoryId)
                .orElseThrow(() -> new InventoryNotFoundException(inventoryId));
        if (inventory.getFilm() == null || !inventory.getFilm().getFilmId().equals(filmId)) {
            throw new ResourceNotFoundException("Inventory", "filmId", filmId);
        }
        return new InventoryResponseDTO(convertToDTO(inventory), filmService.toDTO(inventory.getFilm()));
    }
}
