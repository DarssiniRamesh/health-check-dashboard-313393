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
                // Explicit allow-list of approved browser origins.
                //
                // IMPORTANT:
                // In the Kavia preview environment, the subdomain/port can vary between sessions.
                // Using allowedOrigins(...) requires an exact match and will cause Spring to reply
                // with 403 (Forbidden) and no Access-Control-Allow-Origin header when it doesn't.
                //
                // Use allowedOriginPatterns(...) with narrow wildcards instead of "*".
                .allowedOriginPatterns(
                        "http://localhost:3000",
                        "http://127.0.0.1:3000",
                        // Kavia preview domains (allow any port because the platform can vary it)
                        "https://vscode-internal-*.cloud.kavia.ai:*",
                        "https://*.cloud.kavia.ai:*")
                .allowedMethods("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS")
                // Standard headers used by browsers and typical API clients.
                .allowedHeaders("Accept", "Content-Type", "Authorization", "X-Requested-With", "Origin")
                .exposedHeaders("Location")
                // This service is called without cookies/auth; keep credentials disabled.
                .allowCredentials(false)
                .maxAge(3600);
    }
}
