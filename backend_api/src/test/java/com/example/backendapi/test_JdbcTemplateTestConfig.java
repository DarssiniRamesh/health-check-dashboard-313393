package com.example.backendapi;

import java.sql.Connection;
import java.sql.SQLException;
import javax.sql.DataSource;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.AbstractDataSource;

/**
 * Test-only wiring for {@link JdbcTemplate}.
 *
 * <p>Some full-context tests intentionally disable DataSource auto-configuration to avoid needing a
 * real database. However, several repositories/services are still wired via constructor injection
 * against {@link JdbcTemplate}. This configuration provides a minimal stub {@link DataSource} and
 * a {@link JdbcTemplate} bean to satisfy dependency injection without connecting to any DB.
 */
@TestConfiguration
class test_JdbcTemplateTestConfig {

    @Bean
    DataSource dataSource() {
        return new AbstractDataSource() {
            @Override
            public Connection getConnection() throws SQLException {
                throw new SQLException("Test stub DataSource: no real DB available");
            }

            @Override
            public Connection getConnection(String username, String password) throws SQLException {
                throw new SQLException("Test stub DataSource: no real DB available");
            }
        };
    }

    @Bean
    JdbcTemplate jdbcTemplate(DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }
}
