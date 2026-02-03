package com.example.backendapi.customers;

import com.example.backendapi.api.ApiConstants;
import com.example.backendapi.customers.dto.CreateCustomerRequest;
import com.example.backendapi.customers.dto.CustomerDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/** Customer endpoints (mirrors customers.jsp and CustomerDAO). */
@RestController
@RequestMapping(ApiConstants.API_BASE + "/customers")
@Tag(name = ApiConstants.TAG_CUSTOMERS)
public class CustomersController {

    private final CustomersService customersService;

    public CustomersController(CustomersService customersService) {
        this.customersService = customersService;
    }

    // PUBLIC_INTERFACE
    @GetMapping
    @Operation(summary = "List customers", description = "Returns all customers.")
    public List<CustomerDto> list() {
        /** Lists all customers. */
        return customersService.listCustomers();
    }

    // PUBLIC_INTERFACE
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create customer", description = "Creates a new customer.")
    public CustomerDto create(@Valid @RequestBody CreateCustomerRequest request) {
        /** Creates a customer. */
        return customersService.createCustomer(request);
    }

    // PUBLIC_INTERFACE
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete customer", description = "Deletes a customer by id.")
    public void delete(@PathVariable("id") long id) {
        /** Deletes customer. */
        customersService.deleteCustomer(id);
    }
}
