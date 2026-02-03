package com.example.backendapi;

import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import com.example.backendapi.health.DbHealthResult;
import com.example.backendapi.health.HealthController;
import com.example.backendapi.health.HealthResult;
import com.example.backendapi.health.HealthService;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

/** Web-layer tests for {@link HealthController}. */
@WebMvcTest(controllers = HealthController.class)
class test_HealthControllerWebMvcTest {

    @Autowired private MockMvc mockMvc;

    @MockBean private HealthService healthService;

    @Test
    void health_whenServiceUp_returns200AndUpStatus() throws Exception {
        when(healthService.checkServiceHealth()).thenReturn(HealthResult.up(Map.of()));

        mockMvc.perform(get("/health").accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value("UP"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.timestamp", not(blankOrNullString())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.details").isMap());
    }

    @Test
    void status_whenDbDown_returns503AndDownDatabase() throws Exception {
        when(healthService.checkDatabaseHealth()).thenReturn(DbHealthResult.down(Map.of("error", "no db")));

        mockMvc.perform(get("/status").accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isServiceUnavailable())
                .andExpect(MockMvcResultMatchers.jsonPath("$.database").value("DOWN"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.details.error").value("no db"));
    }

    @Test
    void healthDb_isAliasForStatus() throws Exception {
        when(healthService.checkDatabaseHealth()).thenReturn(DbHealthResult.down(Map.of("error", "no db")));

        mockMvc.perform(get("/health/db").accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isServiceUnavailable())
                .andExpect(MockMvcResultMatchers.jsonPath("$.database").value("DOWN"));
    }
}
