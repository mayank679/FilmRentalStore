package com.film.services;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.film.dto.CustomerDTO;
import com.film.entity.Customer;
import com.film.entity.Store;
import com.film.exception.CustomerEmailAlreadyExistsException;
import com.film.exception.CustomerNotFoundException;
import com.film.exception.StoreNotFoundException;
import com.film.repository.CustomerRepository;
import com.film.repository.StoreRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CustomerService {

    private final CustomerRepository customerRepository;
    private final StoreRepository storeRepository;

    private Customer findOrThrow(Integer id) {
        return customerRepository.findById(id)
                .orElseThrow(() -> new CustomerNotFoundException(id));
    }

    public CustomerDTO.Response toResponse(Customer customer) {
        return CustomerDTO.Response.builder()
                .customerId(customer.getCustomerId())
                .storeId(customer.getStore() != null ? customer.getStore().getStoreId() : null)
                .firstName(customer.getFirstName())
                .lastName(customer.getLastName())
                .email(customer.getEmail())
                .addressId(customer.getAddressId())
                .active(customer.getActive())
                .createDate(customer.getCreateDate())
                .lastUpdate(customer.getLastUpdate())
                .build();
    }

    // GET all (paginated)
    public Page<CustomerDTO.Response> findAllPaginated(int page, int size) {
        return customerRepository.findAll(PageRequest.of(page, size))
                .map(this::toResponse);
    }

    public List<CustomerDTO.Response> getAllCustomers() {
        return customerRepository.findAll()
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    @Transactional
    public CustomerDTO.Response getCustomerById(Integer id) {
        return toResponse(findOrThrow(id));
    }

    public List<CustomerDTO.Response> getCustomerByStore(Integer storeId) {
        return customerRepository.findByStore_StoreId(storeId)
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    public List<CustomerDTO.Response> getCustomersWithActiveStatus(Boolean active) {
        return customerRepository.findByActive(active)
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    // POST
    @Transactional
    public CustomerDTO.Response createCustomer(CustomerDTO.Request request) {
        if (request.getEmail() != null) {
            customerRepository.findByEmail(request.getEmail())
                    .ifPresent(customer -> {
                        throw new CustomerEmailAlreadyExistsException(request.getEmail());
                    });
        }
        Store store = storeRepository.findById(request.getStoreId())
                .orElseThrow(() -> new StoreNotFoundException(request.getStoreId()));
        Customer customer = Customer.builder()
                .store(store)
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .addressId(request.getAddressId())
                .active(request.getActive() != null ? request.getActive() : true)
                .createDate(LocalDateTime.now())
                .lastUpdate(LocalDateTime.now())
                .build();
        return toResponse(customerRepository.save(customer));
    }

    // PUT
    @Transactional
    public CustomerDTO.Response updateCustomer(Integer id, CustomerDTO.Request putRequest) {
        Customer customer = findOrThrow(id);
        if (putRequest.getEmail() != null) {
            customerRepository.findByEmail(putRequest.getEmail())
                    .ifPresent(c -> {
                        throw new CustomerEmailAlreadyExistsException(putRequest.getEmail());
                    });
        }
        Store store = storeRepository.findById(putRequest.getStoreId())
                .orElseThrow(() -> new StoreNotFoundException(putRequest.getStoreId()));
        customer.setStore(store);
        customer.setFirstName(putRequest.getFirstName());
        customer.setLastName(putRequest.getLastName());
        customer.setEmail(putRequest.getEmail());
        customer.setAddressId(putRequest.getAddressId());
        customer.setActive(putRequest.getActive());
        customer.setLastUpdate(LocalDateTime.now());
        return toResponse(customerRepository.save(customer));
    }
}
