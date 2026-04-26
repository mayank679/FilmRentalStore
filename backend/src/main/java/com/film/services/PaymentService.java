package com.film.services;

import com.film.dto.*;
import com.film.entity.*;
import com.film.exception.PaymentNotFoundException;
import com.film.exception.ResourceNotFoundException;
import com.film.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class PaymentService {

    private final PaymentRepository repo;
    private final CustomerRepository customerRepo;
    private final StaffRepository staffRepo;
    private final RentalRepository rentalRepo;

    @Autowired
    private StaffService staffService;

    @Autowired
    private CustomerService customerService;

    @Autowired
    private RentalService rentalService;

    public PaymentService(PaymentRepository repo,
                          CustomerRepository customerRepo,
                          StaffRepository staffRepo,
                          RentalRepository rentalRepo) {
        this.repo = repo;
        this.customerRepo = customerRepo;
        this.staffRepo = staffRepo;
        this.rentalRepo = rentalRepo;
    }

    private PaymentDTO convertToDTO(Payment p) {
        PaymentDTO dto = new PaymentDTO();

        dto.setPaymentId(p.getPaymentId());

        if (p.getCustomer() != null)
            dto.setCustomerId(p.getCustomer().getCustomerId());

        if (p.getStaff() != null)
            dto.setStaffId(p.getStaff().getStaffId());

        if (p.getRental() != null)
            dto.setRentalId(p.getRental().getRentalId());

        dto.setAmount(p.getAmount());
        dto.setPaymentDate(p.getPaymentDate());
        dto.setLastUpdate(p.getLastUpdate());
        return dto;
    }

    private Payment convertToEntity(PaymentDTO dto) {
        Payment p = new Payment();

        if (dto.getCustomerId() != null) {
            Customer c = customerRepo.findById(dto.getCustomerId())
                    .orElseThrow(() -> new ResourceNotFoundException("Customer", "id", dto.getCustomerId()));
            p.setCustomer(c);
        }

        if (dto.getStaffId() != null) {
            Staff s = staffRepo.findById(dto.getStaffId())
                    .orElseThrow(() -> new ResourceNotFoundException("Staff", "id", dto.getStaffId()));
            p.setStaff(s);
        }

        if (dto.getRentalId() != null) {
            Rental r = rentalRepo.findById(dto.getRentalId())
                    .orElseThrow(() -> new ResourceNotFoundException("Rental", "id", dto.getRentalId()));
            p.setRental(r);
        }

        p.setAmount(dto.getAmount());
        p.setPaymentDate(dto.getPaymentDate());

        return p;
    }

    public Page<PaymentDTO> findAllPaginated(int page, int size) {
        return repo.findAll(PageRequest.of(page, size))
                .map(this::convertToDTO);
    }

    public List<PaymentDTO> getAllPayments() {
        return repo.findAll()
                .stream()
                .map(this::convertToDTO)
                .toList();
    }

    public PaymentDTO getPaymentById(Integer id) {
        Payment p = repo.findById(id)
                .orElseThrow(() -> new PaymentNotFoundException(id));
        return convertToDTO(p);
    }

    public List<PaymentDTO> getByCustomerId(Integer customerId) {
        return repo.findByCustomer_CustomerId(customerId)
                .stream().map(this::convertToDTO).toList();
    }

    public List<PaymentDTO> getByStaffId(Integer staffId) {
        return repo.findByStaff_StaffId(staffId)
                .stream().map(this::convertToDTO).toList();
    }

    public List<PaymentDTO> getByRentalId(Integer rentalId) {
        return repo.findByRental_RentalId(rentalId)
                .stream().map(this::convertToDTO).toList();
    }

    public List<PaymentDTO> getByPaymentDate(LocalDateTime date) {
        return repo.findByPaymentDate(date)
                .stream().map(this::convertToDTO).toList();
    }

    public PaymentDTO createPayment(PaymentDTO dto) {
        Payment saved = repo.save(convertToEntity(dto));
        return convertToDTO(saved);
    }

    public PaymentDTO updatePayment(Integer id, PaymentDTO dto) {
        Payment existing = repo.findById(id)
                .orElseThrow(() -> new PaymentNotFoundException(id));

        if (dto.getCustomerId() != null) {
            existing.setCustomer(customerRepo.findById(dto.getCustomerId())
                    .orElseThrow(() -> new ResourceNotFoundException("Customer", "id", dto.getCustomerId())));
        }

        if (dto.getStaffId() != null) {
            existing.setStaff(staffRepo.findById(dto.getStaffId())
                    .orElseThrow(() -> new ResourceNotFoundException("Staff", "id", dto.getStaffId())));
        }

        if (dto.getRentalId() != null) {
            existing.setRental(rentalRepo.findById(dto.getRentalId())
                    .orElseThrow(() -> new ResourceNotFoundException("Rental", "id", dto.getRentalId())));
        }

        existing.setAmount(dto.getAmount());
        existing.setPaymentDate(dto.getPaymentDate());

        return convertToDTO(repo.save(existing));
    }

    public PaymentResponseDTO getPaymentStaff(Integer paymentId, Integer staffId) {

        Payment payment = repo.findById(paymentId)
                .orElseThrow(() -> new PaymentNotFoundException(paymentId));

        if (payment.getStaff() == null ||
                !payment.getStaff().getStaffId().equals(staffId)) {
            throw new ResourceNotFoundException("Payment", "staffId", staffId);
        }

        PaymentDTO paymentDTO = convertToDTO(payment);
        StaffDTO.Response staffDTO = staffService.toResponse(payment.getStaff());

        return new PaymentResponseDTO(paymentDTO, staffDTO);
    }

    public PaymentResponseDTO getPaymentCustomer(Integer paymentId, Integer customerId) {

        Payment payment = repo.findById(paymentId)
                .orElseThrow(() -> new PaymentNotFoundException(paymentId));

        if (payment.getCustomer() == null ||
                !payment.getCustomer().getCustomerId().equals(customerId)) {
            throw new ResourceNotFoundException("Payment", "customerId", customerId);
        }

        PaymentDTO paymentDTO = convertToDTO(payment);
        CustomerDTO.Response customerDTO = customerService.toResponse(payment.getCustomer());

        return new PaymentResponseDTO(paymentDTO, customerDTO);
    }

    public PaymentResponseDTO getPaymentRental(Integer paymentId, Integer rentalId) {

        Payment payment = repo.findById(paymentId)
                .orElseThrow(() -> new PaymentNotFoundException(paymentId));

        if (payment.getRental() == null ||
                !payment.getRental().getRentalId().equals(rentalId)) {
            throw new ResourceNotFoundException("Payment", "rentalId", rentalId);
        }

        PaymentDTO paymentDTO = convertToDTO(payment);
        RentalDTO rentalDTO = rentalService.convertToDTO(payment.getRental());

        return new PaymentResponseDTO(paymentDTO, rentalDTO);
    }
}