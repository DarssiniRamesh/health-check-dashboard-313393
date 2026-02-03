package com.example.backendapi;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

import com.example.backendapi.health.DbHealthResult;
import com.example.backendapi.health.HealthService;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import javax.sql.DataSource;
import org.junit.jupiter.api.Test;

/** Unit tests for {@link HealthService}. */
class test_HealthServiceTest {

    @Test
    void checkDatabaseHealth_whenNoDataSource_returnsDown() {
        HealthService service = new HealthService(null);

        DbHealthResult result = service.checkDatabaseHealth();

        assertThat(result.isUp()).isFalse();
        assertThat(result.details()).containsKey("error");
        assertThat(String.valueOf(result.details().get("error"))).contains("no DataSource");
    }

    @Test
    void checkDatabaseHealth_whenConnectionAndProbeOk_returnsUp() throws Exception {
        DataSource ds = mock(DataSource.class);
        Connection connection = mock(Connection.class);
        Statement stmt = mock(Statement.class);
        ResultSet rs = mock(ResultSet.class);

        when(ds.getConnection()).thenReturn(connection);
        when(connection.isValid(5)).thenReturn(true);

        when(connection.createStatement()).thenReturn(stmt);
        when(stmt.executeQuery("SELECT 1")).thenReturn(rs);
        when(rs.next()).thenReturn(true);

        HealthService service = new HealthService(ds);

        DbHealthResult result = service.checkDatabaseHealth();

        assertThat(result.isUp()).isTrue();

        verify(ds).getConnection();
        verify(connection).createStatement();
        verify(stmt).executeQuery("SELECT 1");
    }

    @Test
    void checkDatabaseHealth_whenSelectProbeFails_returnsDown() throws Exception {
        DataSource ds = mock(DataSource.class);
        Connection connection = mock(Connection.class);
        Statement stmt = mock(Statement.class);

        when(ds.getConnection()).thenReturn(connection);
        when(connection.isValid(5)).thenReturn(true);

        when(connection.createStatement()).thenReturn(stmt);
        when(stmt.executeQuery("SELECT 1")).thenThrow(new java.sql.SQLException("boom"));

        HealthService service = new HealthService(ds);

        DbHealthResult result = service.checkDatabaseHealth();

        assertThat(result.isUp()).isFalse();
        assertThat(String.valueOf(result.details().get("error"))).contains("boom");
    }
}
