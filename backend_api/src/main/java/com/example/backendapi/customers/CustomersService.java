package com.example.backendapi.customers;

import com.example.backendapi.customers.dto.CreateCustomerRequest;
import com.example.backendapi.customers.dto.CustomerDto;
import com.example.backendapi.error.NotFoundException;
import java.util.List;
import org.springframework.stereotype.Service;

/** Business logic for customers (mirrors monolith CustomerDAO behavior). */
@Service
public class CustomersService {

    private final CustomerRepository customerRepository;

    public CustomersService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    // PUBLIC_INTERFACE
    public List<CustomerDto> listCustomers() {
        /** Lists all customers. */
        return customerRepository.findAll();
    }

    // PUBLIC_INTERFACE
    public CustomerDto createCustomer(CreateCustomerRequest request) {
        /** Creates a customer, setting createdAt to now (as monolith does). */
        return customerRepository.insert(request.name(), request.email(), request.address());
    }

    // PUBLIC_INTERFACE
    public void deleteCustomer(long id) {
        /** Deletes a customer, 404 if not found. */
        boolean deleted = customerRepository.delete(id);
        if (!deleted) {
            throw new NotFoundException("Customer not found");
        }
    }

    public CustomerDto getCustomer(long id) {
        return customerRepository.findById(id).orElseThrow(() -> new NotFoundException("Customer not found"));
    }
}
