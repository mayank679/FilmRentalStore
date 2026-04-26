package com.film.service;

import com.film.services.CustomerService;
import com.film.dto.CustomerDTO;
import com.film.entity.Customer;
import com.film.entity.Store;
import com.film.exception.CustomerEmailAlreadyExistsException;
import com.film.exception.CustomerNotFoundException;
import com.film.exception.StoreNotFoundException;
import com.film.repository.CustomerRepository;
import com.film.repository.StoreRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomerServiceTest {

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private StoreRepository storeRepository;

    @InjectMocks
    private CustomerService customerService;

    @Test
    void testGetAllCustomers() {
        Customer customer = new Customer();
        customer.setCustomerId(1);

        when(customerRepository.findAll()).thenReturn(List.of(customer));

        List<CustomerDTO.Response> result = customerService.getAllCustomers();

        assertThat(result).hasSize(1);
    }

    @Test
    void testGetCustomerById() {
        Customer customer = new Customer();
        customer.setCustomerId(1);

        when(customerRepository.findById(1)).thenReturn(Optional.of(customer));

        CustomerDTO.Response result = customerService.getCustomerById(1);

        assertThat(result.getCustomerId()).isEqualTo(1);
    }

    @Test
    void testGetCustomerById_NotFound() {
        when(customerRepository.findById(1)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> customerService.getCustomerById(1))
                .isInstanceOf(CustomerNotFoundException.class);
    }

    @Test
    void testCreateCustomer() {
        CustomerDTO.Request request = new CustomerDTO.Request();
        request.setFirstName("John");
        request.setLastName("Doe");
        request.setEmail("john@test.com");
        request.setStoreId(1);

        Store store = new Store();
        store.setStoreId(1);

        Customer saved = new Customer();
        saved.setCustomerId(1);
        saved.setStore(store);

        when(customerRepository.findByEmail("john@test.com")).thenReturn(Optional.empty());
        when(storeRepository.findById(1)).thenReturn(Optional.of(store));
        when(customerRepository.save(any(Customer.class))).thenReturn(saved);

        CustomerDTO.Response result = customerService.createCustomer(request);

        assertThat(result).isNotNull();
        assertThat(result.getCustomerId()).isEqualTo(1);
    }

    @Test
    void testCreateCustomer_EmailExists() {
        CustomerDTO.Request request = new CustomerDTO.Request();
        request.setEmail("john@test.com");

        when(customerRepository.findByEmail("john@test.com"))
                .thenReturn(Optional.of(new Customer()));

        assertThatThrownBy(() -> customerService.createCustomer(request))
                .isInstanceOf(CustomerEmailAlreadyExistsException.class);
    }

    @Test
    void testCreateCustomer_StoreNotFound() {
        CustomerDTO.Request request = new CustomerDTO.Request();
        request.setEmail("john@test.com");
        request.setStoreId(1);

        when(customerRepository.findByEmail("john@test.com")).thenReturn(Optional.empty());
        when(storeRepository.findById(1)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> customerService.createCustomer(request))
                .isInstanceOf(StoreNotFoundException.class);
    }

    @Test
    void testUpdateCustomer() {
        Customer existing = new Customer();
        existing.setCustomerId(1);

        CustomerDTO.Request request = new CustomerDTO.Request();
        request.setEmail("new@test.com");
        request.setStoreId(1);

        Store store = new Store();
        store.setStoreId(1);

        when(customerRepository.findById(1)).thenReturn(Optional.of(existing));
        when(customerRepository.findByEmail("new@test.com")).thenReturn(Optional.empty());
        when(storeRepository.findById(1)).thenReturn(Optional.of(store));
        when(customerRepository.save(existing)).thenReturn(existing);

        CustomerDTO.Response result = customerService.updateCustomer(1, request);

        assertThat(result).isNotNull();
    }

    @Test
    void testUpdateCustomer_NotFound() {
        when(customerRepository.findById(1)).thenReturn(Optional.empty());

        CustomerDTO.Request request = new CustomerDTO.Request();

        assertThatThrownBy(() -> customerService.updateCustomer(1, request))
                .isInstanceOf(CustomerNotFoundException.class);
    }
}