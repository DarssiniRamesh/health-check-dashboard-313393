package com.example.backendapi;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.backendapi.bootstrap.SampleDataInitializer;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.context.annotation.Import;
import org.springframework.http.ResponseEntity;

/**
 * Full-stack-ish tests: starts the real Spring Boot web server and exercises endpoints over HTTP.
 *
 * <p>Runs without any database by excluding datasource/JPA auto-config for this test class.
 */
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        classes = backendapiApplication.class,
        properties = {
            "spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration,org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration,org.springframework.boot.autoconfigure.data.jpa.JpaRepositoriesAutoConfiguration",
            "spring.sql.init.mode=never"
        })
@Import(test_JdbcTemplateTestConfig.class)
class test_BackendHealthFullStackTest {

    @Autowired private TestRestTemplate restTemplate;

    @MockBean private SampleDataInitializer sampleDataInitializer;

    @Test
    void health_returns200Up() {
        ResponseEntity<Map> response = restTemplate.getForEntity("/health", Map.class);

        assertThat(response.getStatusCode().value()).isEqualTo(200);
        assertThat(response.getBody()).isNotNull();
        assertThat(String.valueOf(response.getBody().get("status"))).isEqualTo("UP");
    }

    @Test
    void status_returns503WhenNoDataSource() {
        ResponseEntity<Map> response = restTemplate.getForEntity("/status", Map.class);

        assertThat(response.getStatusCode().value()).isEqualTo(503);
        assertThat(response.getBody()).isNotNull();
        assertThat(String.valueOf(response.getBody().get("database"))).isEqualTo("DOWN");
    }
}
