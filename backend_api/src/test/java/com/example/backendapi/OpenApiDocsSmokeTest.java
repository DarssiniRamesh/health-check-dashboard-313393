package com.example.backendapi;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.ResponseEntity;

/** Smoke tests for OpenAPI generation endpoints. */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class OpenApiDocsSmokeTest {

    @Autowired private TestRestTemplate restTemplate;

    @Test
    void v3ApiDocsShouldReturn200() {
        ResponseEntity<String> response = restTemplate.getForEntity("/v3/api-docs", String.class);

        assertThat(response.getStatusCode().value()).isEqualTo(200);
        assertThat(response.getBody()).isNotBlank();
        // Minimal sanity check: OpenAPI JSON should contain the openapi version field.
        assertThat(response.getBody()).contains("\"openapi\"");
    }
}
