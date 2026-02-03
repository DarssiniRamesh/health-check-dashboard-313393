package com.example.backendapi;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.backendapi.bootstrap.SampleDataInitializer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.context.annotation.Import;
import org.springframework.http.ResponseEntity;

/**
 * Smoke tests for OpenAPI generation endpoints.
 *
 * <p>This test uses a real embedded web server. We intentionally do NOT require a database
 * connection: the goal is to ensure OpenAPI generation works even when DB config is absent.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = backendapiApplication.class)
@Import(test_JdbcTemplateTestConfig.class)
class OpenApiDocsSmokeTest {

    @Autowired private TestRestTemplate restTemplate;

    /**
     * The application registers {@link SampleDataInitializer} as an {@code ApplicationRunner}.
     *
     * <p>In test mode we exclude datasource/JPA auto-config via {@code application-test.properties},
     * which means there is no {@code JdbcTemplate}. Mocking this runner prevents Spring from trying
     * to instantiate the real initializer (and its repository dependencies).
     */
    @MockBean private SampleDataInitializer sampleDataInitializer;

    @Test
    void v3ApiDocsShouldReturn200() {
        ResponseEntity<String> response = restTemplate.getForEntity("/v3/api-docs", String.class);

        assertThat(response.getStatusCode().value()).isEqualTo(200);
        assertThat(response.getBody()).isNotBlank();
        // Minimal sanity check: OpenAPI JSON should contain the openapi version field.
        assertThat(response.getBody()).contains("\"openapi\"");
    }
}
