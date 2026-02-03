package com.example.backendapi.users;

import com.example.backendapi.users.dto.UserDto;
import java.util.List;
import java.util.Optional;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

/** JDBC repository for users table. */
@Repository
public class UserRepository {

    private static final RowMapper<UserDto> USER_MAPPER =
            (rs, rowNum) -> new UserDto(rs.getLong("id"), rs.getString("email"), rs.getString("name"));

    private final JdbcTemplate jdbcTemplate;

    public UserRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public long count() {
        Long cnt = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM public.users", Long.class);
        return cnt == null ? 0L : cnt;
    }

    public List<UserDto> findAll() {
        return jdbcTemplate.query("SELECT id, email, name FROM public.users ORDER BY id ASC", USER_MAPPER);
    }

    public Optional<UserDto> findById(long id) {
        List<UserDto> rows =
                jdbcTemplate.query("SELECT id, email, name FROM public.users WHERE id = ?", USER_MAPPER, id);
        return rows.stream().findFirst();
    }

    public Optional<UserDto> findByEmail(String email) {
        List<UserDto> rows =
                jdbcTemplate.query(
                        "SELECT id, email, name FROM public.users WHERE email = ? LIMIT 1", USER_MAPPER, email);
        return rows.stream().findFirst();
    }

    public UserDto insert(String email, String name) {
        // Postgres: RETURNING to fetch generated id.
        return jdbcTemplate.queryForObject(
                "INSERT INTO public.users (email, name) VALUES (?, ?) RETURNING id, email, name",
                USER_MAPPER,
                email,
                name);
    }

    public boolean delete(long id) {
        return jdbcTemplate.update("DELETE FROM public.users WHERE id = ?", id) > 0;
    }

    public Optional<UserDto> update(long id, String email, String name) {
        int updated = jdbcTemplate.update("UPDATE public.users SET email = ?, name = ? WHERE id = ?", email, name, id);
        if (updated <= 0) {
            return Optional.empty();
        }
        return findById(id);
    }
}
