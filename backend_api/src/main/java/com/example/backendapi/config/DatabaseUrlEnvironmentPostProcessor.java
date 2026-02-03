package com.example.backendapi.config;

import java.net.URI;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.core.Ordered;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.util.StringUtils;

/**
 * Maps common "single URL" Postgres env vars (e.g. DATABASE_URL provided by Neon/hosting platforms)
 * into Spring Boot's standard {@code spring.datasource.*} properties.
 *
 * <p>This processor converts DATABASE_URL (or NEON_DATABASE_URL, POSTGRES_URL, etc.) from
 * {@code postgresql://user:pass@host:port/db} format into JDBC format with sslmode=require.
 *
 * <p>Explicit SPRING_DATASOURCE_* env vars take precedence. If neither DATABASE_URL variants
 * nor SPRING_DATASOURCE_URL are provided, the app will fail to start (no localhost fallback).
 */
public class DatabaseUrlEnvironmentPostProcessor implements EnvironmentPostProcessor, Ordered {

    private static final String PROPERTY_SOURCE_NAME = "databaseUrlOverride";

    @Override
    public int getOrder() {
        // Run early (but after system env is available) so our overrides take precedence
        // over application.properties defaults.
        return Ordered.HIGHEST_PRECEDENCE + 10;
    }

    @Override
    public void postProcessEnvironment(
            ConfigurableEnvironment environment, SpringApplication application) {

        // Respect explicit Spring datasource env vars/config if provided.
        if (StringUtils.hasText(System.getenv("SPRING_DATASOURCE_URL"))
                || StringUtils.hasText(System.getenv("SPRING_DATASOURCE_USERNAME"))
                || StringUtils.hasText(System.getenv("SPRING_DATASOURCE_PASSWORD"))) {
            return;
        }

        String raw = firstNonBlank(
                System.getenv("DATABASE_URL"),
                System.getenv("NEON_DATABASE_URL"),
                System.getenv("POSTGRES_URL"),
                System.getenv("POSTGRES_PRISMA_URL"),
                System.getenv("POSTGRES_URL_NON_POOLING"));

        if (!StringUtils.hasText(raw)) {
            return;
        }

        // Some providers/documentation supply the connection as a full psql command, e.g.:
        //   psql 'postgresql://user:pass@host/db?sslmode=require'
        // or
        //   psql postgresql://user:pass@host/db
        //
        // Normalize that into just the URL so our parser can reliably build a JDBC URL.
        String normalizedRaw = normalizeDatabaseUrl(raw);

        ParsedJdbc parsed = parseToJdbc(normalizedRaw);
        if (parsed == null || !StringUtils.hasText(parsed.jdbcUrl())) {
            return;
        }

        Map<String, Object> props = new LinkedHashMap<>();
        props.put("spring.datasource.url", parsed.jdbcUrl());

        // Only set username/password if present in the URL and not already set via env.
        if (StringUtils.hasText(parsed.username()) && !StringUtils.hasText(System.getenv("SPRING_DATASOURCE_USERNAME"))) {
            props.put("spring.datasource.username", parsed.username());
        }
        if (StringUtils.hasText(parsed.password()) && !StringUtils.hasText(System.getenv("SPRING_DATASOURCE_PASSWORD"))) {
            props.put("spring.datasource.password", parsed.password());
        }

        // Put our properties with highest precedence so they override application.properties defaults.
        environment.getPropertySources().addFirst(new MapPropertySource(PROPERTY_SOURCE_NAME, props));
    }

    private static String firstNonBlank(String... values) {
        for (String v : values) {
            if (StringUtils.hasText(v)) {
                return v.trim();
            }
        }
        return null;
    }

    private static String normalizeDatabaseUrl(String raw) {
        String trimmed = raw == null ? null : raw.trim();
        if (!StringUtils.hasText(trimmed)) {
            return trimmed;
        }

        // Strip leading "psql" (and optional flags) if present.
        // Examples:
        //  - psql postgresql://...
        //  - psql 'postgresql://...'
        //  - psql --set=sslmode=require postgresql://...   (rare, but be defensive)
        if (trimmed.startsWith("psql")) {
            // Remove the leading "psql" token.
            String rest = trimmed.substring(4).trim();

            // Remove any leading flags like -X, -v, --set=... until we hit something that looks like a URL.
            // This is intentionally simple: we only need to support common copy/paste forms.
            while (rest.startsWith("-")) {
                int nextSpace = rest.indexOf(' ');
                if (nextSpace < 0) {
                    // Only flags present and no URL.
                    return "";
                }
                rest = rest.substring(nextSpace + 1).trim();
            }
            trimmed = rest;
        }

        // Strip surrounding single/double quotes.
        if ((trimmed.startsWith("'") && trimmed.endsWith("'")) || (trimmed.startsWith("\"") && trimmed.endsWith("\""))) {
            trimmed = trimmed.substring(1, trimmed.length() - 1);
        }

        return trimmed.trim();
    }

    private static ParsedJdbc parseToJdbc(String rawUrl) {
        try {
            // Accept:
            // - postgres://user:pass@host:port/db?sslmode=require
            // - postgresql://...
            // - jdbc:postgresql://...
            if (rawUrl.startsWith("jdbc:postgresql:")) {
                // Ensure sslmode=require is present for Neon when using a raw JDBC URL.
                return new ParsedJdbc(ensureSslMode(rawUrl), null, null);
            }

            String normalized = rawUrl.replaceFirst("^postgres://", "postgresql://");

            URI uri = URI.create(normalized);

            if (!StringUtils.hasText(uri.getScheme())
                    || (!uri.getScheme().equals("postgresql"))) {
                return null;
            }

            String host = uri.getHost();
            int port = uri.getPort() > 0 ? uri.getPort() : 5432;

            String path = uri.getPath(); // includes leading '/'
            String db = (path != null && path.length() > 1) ? path.substring(1) : "";

            String user = null;
            String pass = null;
            if (StringUtils.hasText(uri.getUserInfo())) {
                String[] parts = uri.getUserInfo().split(":", 2);
                user = urlDecode(parts[0]);
                if (parts.length > 1) {
                    pass = urlDecode(parts[1]);
                }
            }

            Map<String, String> query = parseQuery(uri.getRawQuery());

            // Build jdbc url.
            StringBuilder jdbc = new StringBuilder();
            jdbc.append("jdbc:postgresql://").append(host).append(":").append(port).append("/").append(db);

            // Preserve existing query params; ensure sslmode=require for Neon.
            if (!query.containsKey("sslmode")) {
                query.put("sslmode", "require");
            }

            if (!query.isEmpty()) {
                jdbc.append("?");
                boolean first = true;
                for (Map.Entry<String, String> e : query.entrySet()) {
                    if (!first) {
                        jdbc.append("&");
                    }
                    first = false;
                    jdbc.append(urlEncode(e.getKey())).append("=").append(urlEncode(e.getValue()));
                }
            }

            return new ParsedJdbc(jdbc.toString(), user, pass);
        } catch (RuntimeException ex) {
            // Be defensive: never prevent app startup due to parsing issues.
            return null;
        }
    }

    private static String ensureSslMode(String jdbcUrl) {
        // If URL already has sslmode=..., keep it.
        String lower = jdbcUrl.toLowerCase();
        if (lower.contains("sslmode=")) {
            return jdbcUrl;
        }

        // Append sslmode=require safely whether query already exists or not.
        if (jdbcUrl.contains("?")) {
            return jdbcUrl + "&sslmode=require";
        }
        return jdbcUrl + "?sslmode=require";
    }

    private static Map<String, String> parseQuery(String rawQuery) {
        Map<String, String> out = new LinkedHashMap<>();
        if (!StringUtils.hasText(rawQuery)) {
            return out;
        }
        String[] parts = rawQuery.split("&");
        for (String p : parts) {
            if (!StringUtils.hasText(p)) {
                continue;
            }
            String[] kv = p.split("=", 2);
            String k = urlDecode(kv[0]);
            String v = kv.length > 1 ? urlDecode(kv[1]) : "";
            if (StringUtils.hasText(k)) {
                out.put(k, v);
            }
        }
        return out;
    }

    private static String urlDecode(String s) {
        return URLDecoder.decode(s, StandardCharsets.UTF_8);
    }

    private static String urlEncode(String s) {
        // Minimal encoding (space etc.) for query params.
        return java.net.URLEncoder.encode(s, StandardCharsets.UTF_8);
    }

    private record ParsedJdbc(String jdbcUrl, String username, String password) {}
}
