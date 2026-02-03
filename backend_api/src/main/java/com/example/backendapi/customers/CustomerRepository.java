package com.example.backendapi.customers;

import com.example.backendapi.customers.dto.CustomerDto;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

/** JDBC repository for customers table. */
@Repository
public class CustomerRepository {

    private static final RowMapper<CustomerDto> CUSTOMER_MAPPER =
            (rs, rowNum) -> {
                Timestamp ts = rs.getTimestamp("created_at");
                Instant createdAt = ts == null ? null : ts.toInstant();
                return new CustomerDto(
                        rs.getLong("id"),
                        rs.getString("name"),
                        rs.getString("email"),
                        rs.getString("address"),
                        createdAt);
            };

    private final JdbcTemplate jdbcTemplate;

    public CustomerRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public long count() {
        Long cnt = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM public.customers", Long.class);
        return cnt == null ? 0L : cnt;
    }

    public List<CustomerDto> findAll() {
        return jdbcTemplate.query(
                "SELECT id, name, email, address, created_at FROM public.customers ORDER BY id ASC", CUSTOMER_MAPPER);
    }

    public Optional<CustomerDto> findById(long id) {
        List<CustomerDto> rows =
                jdbcTemplate.query(
                        "SELECT id, name, email, address, created_at FROM public.customers WHERE id = ?",
                        CUSTOMER_MAPPER,
                        id);
        return rows.stream().findFirst();
    }

    public CustomerDto insert(String name, String email, String address) {
        return jdbcTemplate.queryForObject(
                "INSERT INTO public.customers (name, email, address, created_at) VALUES (?, ?, ?, NOW()) "
                        + "RETURNING id, name, email, address, created_at",
                CUSTOMER_MAPPER,
                name,
                email,
                address);
    }

    public boolean delete(long id) {
        return jdbcTemplate.update("DELETE FROM public.customers WHERE id = ?", id) > 0;
    }
}
