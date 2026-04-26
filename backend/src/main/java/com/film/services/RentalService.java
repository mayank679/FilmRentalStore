package com.film.services;

import com.film.dto.*;
import com.film.entity.Customer;
import com.film.entity.Inventory;
import com.film.entity.Rental;
import com.film.entity.Staff;
import com.film.exception.RentalNotFoundException;
import com.film.exception.ResourceNotFoundException;
import com.film.repository.CustomerRepository;
import com.film.repository.InventoryRepository;
import com.film.repository.RentalRepository;
import com.film.repository.StaffRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class RentalService
{
    @Autowired
    private RentalRepository repo;

    @Autowired
    private InventoryRepository inventoryRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private StaffRepository staffRepository;

    @Autowired
    private InventoryService inventoryService;

    @Autowired
    private CustomerService customerService;

    @Autowired
    private StaffService staffService;

    public RentalService(RentalRepository repo)
    {
        this.repo = repo;
    }

    public RentalDTO convertToDTO(Rental r)
    {
        RentalDTO dto = new RentalDTO();

        dto.setRentalId(r.getRentalId());
        dto.setInventoryId(
                r.getInventory() != null ? r.getInventory().getInventoryId():null
        );
        dto.setCustomerId(
                r.getCustomer() != null ? r.getCustomer().getCustomerId(): null
        );
        dto.setStaffId(
                r.getStaff() != null ? r.getStaff().getStaffId():null
        );

        dto.setRentalDate(r.getRentalDate());

        if(r.getReturnDate()!=null)
        {
            dto.setReturnDate(r.getReturnDate());
        }
        return dto;
    }

    public List<RentalDTO> getAllRentals()
    {
        List<Rental> rentals = repo.findAll();
        return rentals.stream().map(this::convertToDTO).toList();
    }

    public List<RentalDTO> getRentalsWithPagination(int page, int size)
    {
        Pageable pageable = PageRequest.of(page,size);
        Page<Rental> rentalPage = repo.findAll(pageable);
        return rentalPage.getContent()
                .stream()
                .map(this::convertToDTO)
                .toList();
    }

    public Page<RentalDTO> findAllPaginated(int page, int size) {
        return repo.findAll(PageRequest.of(page, size))
                .map(this::convertToDTO);
    }

    public RentalDTO getRentalById(Integer id) {
        Rental rental = repo.findById(id)
                .orElseThrow(() -> new RentalNotFoundException(id));
        return convertToDTO(rental);
    }

    public List<RentalDTO> getByCustomerId(Integer customerId)
    {
        return repo.findByCustomer_CustomerId(customerId)
                .stream()
                .map(this::convertToDTO)
                .toList();
    }

    public List<RentalDTO> getByInventoryId(Integer inventoryId)
    {
        return repo.findByInventory_InventoryId(inventoryId)
                .stream()
                .map(this::convertToDTO)
                .toList();
    }

    public List<RentalDTO> getByStaffId(Integer staffId)
    {
        return repo.findByStaff_StaffId(staffId)
                .stream()
                .map(this::convertToDTO)
                .toList();
    }

    public List<RentalDTO> getByRentalDateRange(LocalDateTime start, LocalDateTime end)
    {
        return repo.findByRentalDateBetween(start, end)
                .stream()
                .map(this::convertToDTO)
                .toList();
    }

    public List<RentalDTO> getByReturnDateRange(LocalDateTime start, LocalDateTime end)
    {
        return repo.findByReturnDateBetween(start, end)
                .stream()
                .map(this::convertToDTO)
                .toList();
    }

    public List<RentalDTO> getByLastUpdateRange(LocalDateTime start, LocalDateTime end) {

        return repo.findByLastUpdateBetween(start, end)
                .stream()
                .map(this::convertToDTO)
                .toList();
    }

    public RentalResponseDTO getRentalInventory(Integer rentalId, Integer inventoryId) {

        Rental rental = repo.findById(rentalId)
                .orElseThrow(() -> new RentalNotFoundException(rentalId));

        if (rental.getInventory() == null ||
                !rental.getInventory().getInventoryId().equals(inventoryId)) {
            throw new ResourceNotFoundException("Rental", "inventoryId", inventoryId);
        }

        RentalDTO rentalDTO = convertToDTO(rental);
        InventoryDTO inventoryDTO = inventoryService.convertToDTO(rental.getInventory());

        return new RentalResponseDTO(rentalDTO, inventoryDTO);
    }

    public RentalResponseDTO getRentalCustomer(Integer rentalId, Integer customerId) {

        Rental rental = repo.findById(rentalId)
                .orElseThrow(() -> new RentalNotFoundException(rentalId));

        if (rental.getCustomer() == null ||
                !rental.getCustomer().getCustomerId().equals(customerId)) {
            throw new ResourceNotFoundException("Rental", "customerId", customerId);
        }

        RentalDTO rentalDTO = convertToDTO(rental);
        CustomerDTO.Response customerDTO = customerService.toResponse(rental.getCustomer());

        return new RentalResponseDTO(rentalDTO, customerDTO);
    }

    public RentalResponseDTO getRentalStaff(Integer rentalId, Integer staffId)
    {
        Rental rental = repo.findById(rentalId)
                .orElseThrow(() -> new RentalNotFoundException(rentalId));

        if (rental.getStaff() == null ||
                !rental.getStaff().getStaffId().equals(staffId)) {
            throw new ResourceNotFoundException("Rental", "staffId", staffId);
        }

        RentalDTO rentalDTO = convertToDTO(rental);
        StaffDTO.Response staffDTO = staffService.toResponse(rental.getStaff());

        return new RentalResponseDTO(rentalDTO, staffDTO);
    }

    public RentalDTO createRental(RentalDTO dto) {

        Rental rental = new Rental();

        rental.setRentalDate(dto.getRentalDate());

        Inventory inventory = inventoryRepository.findById(dto.getInventoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Inventory", "id", dto.getInventoryId()));
        rental.setInventory(inventory);

        Customer customer = customerRepository.findById(dto.getCustomerId())
                .orElseThrow(() -> new ResourceNotFoundException("Customer", "id", dto.getCustomerId()));
        rental.setCustomer(customer);

        Staff staff = staffRepository.findById(dto.getStaffId())
                .orElseThrow(() -> new ResourceNotFoundException("Staff", "id", dto.getStaffId()));
        rental.setStaff(staff);

        Rental saved = repo.save(rental);

        return convertToDTO(saved);
    }

    public RentalDTO updateRental(Integer id, RentalDTO dto) {

        Rental rental = repo.findById(id)
                .orElseThrow(() -> new RentalNotFoundException(id));

        rental.setRentalDate(dto.getRentalDate());
        rental.setReturnDate(dto.getReturnDate());

        Inventory inventory = inventoryRepository.findById(dto.getInventoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Inventory", "id", dto.getInventoryId()));
        rental.setInventory(inventory);

        Customer customer = customerRepository.findById(dto.getCustomerId())
                .orElseThrow(() -> new ResourceNotFoundException("Customer", "id", dto.getCustomerId()));
        rental.setCustomer(customer);

        Staff staff = staffRepository.findById(dto.getStaffId())
                .orElseThrow(() -> new ResourceNotFoundException("Staff", "id", dto.getStaffId()));
        rental.setStaff(staff);

        return convertToDTO(repo.save(rental));
    }
}