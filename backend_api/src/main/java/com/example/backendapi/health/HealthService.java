package com.example.backendapi.health;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;
import javax.sql.DataSource;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;

/** Service implementing application and database health checks. */
@Service
public class HealthService {

    private final @Nullable DataSource dataSource;

    public HealthService(@Nullable DataSource dataSource) {
        // DataSource may be absent if DB config is missing/invalid. We still want the app to start
        // and expose /health; DB endpoints should return DOWN in this case.
        this.dataSource = dataSource;
    }

    // PUBLIC_INTERFACE
    public HealthResult checkServiceHealth() {
        /**
         * Overall service health check.
         *
         * <p>For minimal flow we treat service as UP if the app can execute code paths. Future
         * expansion: include component checks (DB, downstream APIs).
         */
        return HealthResult.up(Map.of());
    }

    // PUBLIC_INTERFACE
    public DbHealthResult checkDatabaseHealth() {
        /**
         * Database health check.
         *
         * <p>Uses {@link DataSource} to attempt a connection. If successful, reports UP.
         *
         * <p>If no DataSource is configured, returns DOWN without throwing so the app can still boot.
         */
        if (dataSource == null) {
            return DbHealthResult.down(Map.of("error", "Database is not configured (no DataSource)"));
        }

        try (Connection connection = dataSource.getConnection()) {
            boolean valid;
            try {
                valid = connection.isValid(2);
            } catch (SQLException ignored) {
                // Some drivers may not support isValid reliably; if we got a connection,
                // consider it UP.
                valid = true;
            }

            if (!valid) {
                return DbHealthResult.down(Map.of("error", "Connection is not valid"));
            }
            return DbHealthResult.up(Map.of());
        } catch (SQLException ex) {
            return DbHealthResult.down(
                    Map.of(
                            "error", ex.getMessage() == null ? "Database connection failed" : ex.getMessage()));
        }
    }
}
