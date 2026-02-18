package com.example.backendapi;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import com.example.backendapi.customers.CustomersController;
import com.example.backendapi.customers.CustomersService;
import com.example.backendapi.customers.dto.CustomerDto;
import java.time.Instant;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

/** Web-layer tests for {@link CustomersController}. */
@WebMvcTest(controllers = CustomersController.class)
class test_CustomersControllerWebMvcTest {

    @Autowired private MockMvc mockMvc;

    @MockBean private CustomersService customersService;

    @Test
    void list_returnsArrayOfCustomers() throws Exception {
        when(customersService.listCustomers())
                .thenReturn(
                        List.of(
                                new CustomerDto(1L, "Acme", "billing@acme.com", "123 St", Instant.parse("2024-01-01T00:00:00Z"))));

        mockMvc.perform(get("/api/customers").accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$", hasSize(1)))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].id").value(1))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].name").value("Acme"));
    }

    @Test
    void create_whenValid_returns201AndBody() throws Exception {
        when(customersService.createCustomer(any()))
                .thenReturn(new CustomerDto(2L, "NewCo", "hello@newco.com", "Somewhere", Instant.parse("2024-01-02T00:00:00Z")));

        mockMvc.perform(
                        post("/api/customers")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(
                                        """
                                        {"name":"NewCo","email":"hello@newco.com","address":"Somewhere"}
                                        """))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(2))
                .andExpect(MockMvcResultMatchers.jsonPath("$.email").value("hello@newco.com"));
    }

    @Test
    void create_whenMissingRequiredFields_returns400() throws Exception {
        mockMvc.perform(
                        post("/api/customers")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("{}"))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());

        verify(customersService, never()).createCustomer(any());
    }

    @Test
    void delete_returns204() throws Exception {
        mockMvc.perform(delete("/api/customers/123"))
                .andExpect(MockMvcResultMatchers.status().isNoContent());

        verify(customersService).deleteCustomer(123L);
    }
}
