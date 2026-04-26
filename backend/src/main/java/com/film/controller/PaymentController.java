package com.film.controller;

import com.film.dto.PaymentDTO;
import com.film.dto.PaymentResponseDTO;
import com.film.dto.ResponseDTO;
import com.film.entity.Payment;
import com.film.services.PaymentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import org.springframework.data.domain.Page;
import java.util.List;

@RestController
@RequestMapping("/api/payment")
public class PaymentController {

    private final PaymentService service;

    public PaymentController(PaymentService service) {
        this.service = service;
    }

    // GET all payments (paginated)
    @GetMapping("/paged")
    public ResponseEntity<Page<PaymentDTO>> getAllPaymentsPaged(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(service.findAllPaginated(page, size));
    }

    @GetMapping
    public List<PaymentDTO> getAll() {
        return service.getAllPayments();
    }

    @GetMapping("/{id}")
    public PaymentDTO getById(@PathVariable Integer id) {
        return service.getPaymentById(id);
    }

    @GetMapping("/customer/{customerId}")
    public List<PaymentDTO> getByCustomer(@PathVariable Integer customerId) {
        return service.getByCustomerId(customerId);
    }

    @GetMapping("/staff/{staffId}")
    public List<PaymentDTO> getByStaff(@PathVariable Integer staffId) {
        return service.getByStaffId(staffId);
    }

    @GetMapping("/rental/{rentalId}")
    public List<PaymentDTO> getByRental(@PathVariable Integer rentalId) {
        return service.getByRentalId(rentalId);
    }

    @GetMapping("/date")
    public List<PaymentDTO> getByDate(@RequestParam LocalDateTime date) {
        return service.getByPaymentDate(date);
    }

    @PostMapping
    public PaymentDTO create(@RequestBody PaymentDTO dto) {
        return service.createPayment(dto);
    }

    @PutMapping("/{id}")
    public PaymentDTO update(@PathVariable Integer id,
                             @RequestBody PaymentDTO dto) {
        return service.updatePayment(id, dto);
    }

    @GetMapping("/payment-staff")
    public ResponseEntity<ResponseDTO<PaymentResponseDTO>> getPaymentStaff(
            @RequestParam Integer paymentId,
            @RequestParam Integer staffId) {

        PaymentResponseDTO data =
                service.getPaymentStaff(paymentId, staffId);

        return ResponseEntity.ok(
                new ResponseDTO<>("SUCCESS", "Payment + Staff fetched successfully", data)
        );
    }

    @GetMapping("/payment-customer")
    public ResponseEntity<ResponseDTO<PaymentResponseDTO>> getPaymentCustomer(
            @RequestParam Integer paymentId,
            @RequestParam Integer customerId) {

        PaymentResponseDTO data =
                service.getPaymentCustomer(paymentId, customerId);

        return ResponseEntity.ok(
                new ResponseDTO<>("SUCCESS", "Payment + Customer fetched successfully", data)
        );
    }

    @GetMapping("/payment-rental")
    public ResponseEntity<ResponseDTO<PaymentResponseDTO>> getPaymentRental(
            @RequestParam Integer paymentId,
            @RequestParam Integer rentalId) {

        PaymentResponseDTO data =
                service.getPaymentRental(paymentId, rentalId);

        return ResponseEntity.ok(
                new ResponseDTO<>("SUCCESS", "Payment + Rental fetched successfully", data)
        );
    }

}