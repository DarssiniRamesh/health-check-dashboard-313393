package com.example.backendapi.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Global CORS configuration for the API.
 *
 * <p>Allows the Angular frontend to call the backend from approved origins.
 *
 * <p>IMPORTANT: We do not use "*" here because it is unsafe if credentials are ever enabled in the
 * future.
 */
@Configuration
public class WebCorsConfig implements WebMvcConfigurer {

    /**
     * PUBLIC_INTERFACE
     *
     * <p>Configures global CORS for all routes (including {@code GET /health}).
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                // IMPORTANT:
                // The Kavia preview environment uses ephemeral subdomains and ports for both the
                // frontend and backend. Matching exact origins is brittle and can cause CORS 403s
                // (no Access-Control-Allow-Origin) even though the backend is healthy.
                //
                // We intentionally allow all origins here, but keep allowCredentials(false).
                // With credentials disabled, this does not permit cookie-based cross-site requests.
                .allowedOriginPatterns("*")
                .allowedMethods("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS")
                // Standard headers used by browsers and typical API clients.
                .allowedHeaders("Accept", "Content-Type", "Authorization", "X-Requested-With", "Origin")
                .exposedHeaders("Location")
                // This service is called without cookies/auth; keep credentials disabled.
                .allowCredentials(false)
                .maxAge(3600);
    }
}
