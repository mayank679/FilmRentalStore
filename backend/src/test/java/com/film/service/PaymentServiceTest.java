package com.film.service;

import com.film.dto.PaymentDTO;
import com.film.entity.*;
import com.film.exception.PaymentNotFoundException;
import com.film.exception.ResourceNotFoundException;
import com.film.repository.*;
import com.film.services.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {

    @Mock
    private PaymentRepository repo;

    @Mock
    private CustomerRepository customerRepo;

    @Mock
    private StaffRepository staffRepo;

    @Mock
    private RentalRepository rentalRepo;

    @Mock
    private StaffService staffService;

    @Mock
    private CustomerService customerService;

    @Mock
    private RentalService rentalService;

    private PaymentService service;

    @BeforeEach
    void setup() {
        service = new PaymentService(repo, customerRepo, staffRepo, rentalRepo);

        ReflectionTestUtils.setField(service, "staffService", staffService);
        ReflectionTestUtils.setField(service, "customerService", customerService);
        ReflectionTestUtils.setField(service, "rentalService", rentalService);
    }

    private Payment getPayment(Integer id) {
        Payment p = new Payment();
        p.setPaymentId(id);

        Customer c = new Customer();
        c.setCustomerId(1);
        p.setCustomer(c);

        Staff s = new Staff();
        s.setStaffId(1);
        p.setStaff(s);

        Rental r = new Rental();
        r.setRentalId(1);
        p.setRental(r);

        p.setAmount(BigDecimal.valueOf(10));
        p.setPaymentDate(LocalDateTime.now());
        p.setLastUpdate(LocalDateTime.now());
        return p;
    }

    @Test
    void testGetAllPayments() {
        when(repo.findAll()).thenReturn(List.of(getPayment(1)));

        var result = service.getAllPayments();

        assertThat(result).hasSize(1);
    }

    @Test
    void testGetPaymentByIdSuccess() {
        when(repo.findById(1)).thenReturn(Optional.of(getPayment(1)));

        var result = service.getPaymentById(1);

        assertThat(result.getPaymentId()).isEqualTo(1);
    }

    @Test
    void testGetPaymentByIdNotFound() {
        when(repo.findById(1)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.getPaymentById(1))
                .isInstanceOf(PaymentNotFoundException.class);
    }

    @Test
    void testGetByCustomerId() {
        when(repo.findByCustomer_CustomerId(1))
                .thenReturn(List.of(getPayment(1)));

        var result = service.getByCustomerId(1);

        assertThat(result).hasSize(1);
    }

    @Test
    void testCreatePayment() {
        PaymentDTO dto = new PaymentDTO();
        dto.setCustomerId(1);
        dto.setStaffId(1);
        dto.setRentalId(1);
        dto.setAmount(BigDecimal.valueOf(10));
        dto.setPaymentDate(LocalDateTime.now());

        when(customerRepo.findById(1)).thenReturn(Optional.of(new Customer()));
        when(staffRepo.findById(1)).thenReturn(Optional.of(new Staff()));
        when(rentalRepo.findById(1)).thenReturn(Optional.of(new Rental()));
        when(repo.save(any())).thenAnswer(i -> {
            Payment p = i.getArgument(0);
            p.setPaymentId(1);
            return p;
        });

        var result = service.createPayment(dto);

        assertThat(result.getPaymentId()).isEqualTo(1);
    }

    @Test
    void testUpdatePaymentSuccess() {
        Payment existing = getPayment(1);

        PaymentDTO dto = new PaymentDTO();
        dto.setCustomerId(1);
        dto.setStaffId(1);
        dto.setRentalId(1);
        dto.setAmount(BigDecimal.valueOf(20));
        dto.setPaymentDate(LocalDateTime.now());

        when(repo.findById(1)).thenReturn(Optional.of(existing));
        when(customerRepo.findById(1)).thenReturn(Optional.of(new Customer()));
        when(staffRepo.findById(1)).thenReturn(Optional.of(new Staff()));
        when(rentalRepo.findById(1)).thenReturn(Optional.of(new Rental()));
        when(repo.save(any())).thenAnswer(i -> i.getArgument(0));

        var result = service.updatePayment(1, dto);

        assertThat(result.getAmount()).isEqualTo(BigDecimal.valueOf(20));
    }

    @Test
    void testUpdatePaymentNotFound() {
        when(repo.findById(1)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.updatePayment(1, new PaymentDTO()))
                .isInstanceOf(PaymentNotFoundException.class);
    }

    @Test
    void testGetPaymentStaffSuccess() {
        Payment p = getPayment(1);

        when(repo.findById(1)).thenReturn(Optional.of(p));
        when(staffService.toResponse(any())).thenReturn(null);

        var result = service.getPaymentStaff(1, 1);

        assertThat(result).isNotNull();
    }

    @Test
    void testGetPaymentStaffMismatch() {
        Payment p = getPayment(1);

        when(repo.findById(1)).thenReturn(Optional.of(p));

        assertThatThrownBy(() -> service.getPaymentStaff(1, 2))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void testGetPaymentCustomerSuccess() {
        Payment p = getPayment(1);

        when(repo.findById(1)).thenReturn(Optional.of(p));
        when(customerService.toResponse(any())).thenReturn(null);

        var result = service.getPaymentCustomer(1, 1);

        assertThat(result).isNotNull();
    }

    @Test
    void testGetPaymentRentalSuccess() {
        Payment p = getPayment(1);

        when(repo.findById(1)).thenReturn(Optional.of(p));
        when(rentalService.convertToDTO(any())).thenReturn(null);

        var result = service.getPaymentRental(1, 1);

        assertThat(result).isNotNull();
    }
}