package com.film.exception;

public class PaymentNotFoundException extends ResourceNotFoundException {

    public PaymentNotFoundException(Integer id) {
        super("Payment", "id", id);
    }
}
