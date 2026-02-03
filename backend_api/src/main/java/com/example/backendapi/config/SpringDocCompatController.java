package com.example.backendapi.config;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.RedirectView;
import org.springframework.web.util.UriComponentsBuilder;

/**
 * Compatibility redirects for OpenAPI endpoints.
 *
 * <p>Some environments/tools expect SpringDoc to expose OpenAPI JSON at either
 * {@code /openapi.json} or {@code /api-docs}. This app standardizes on the SpringDoc default
 * {@code /v3/api-docs}.
 *
 * <p>This controller redirects old/alternate paths to {@code /v3/api-docs}, preserving the incoming
 * scheme/host/port (including X-Forwarded-* headers).
 */
@RestController
public class SpringDocCompatController {

    /**
     * PUBLIC_INTERFACE
     *
     * <p>Redirects {@code /openapi.json} to {@code /v3/api-docs}.
     *
     * @param request incoming HTTP request (used to preserve forwarded host/scheme/port)
     * @return redirect response to the canonical OpenAPI endpoint
     */
    @GetMapping("/openapi.json")
    public RedirectView openApiJson(HttpServletRequest request) {
        return redirectToV3ApiDocs(request);
    }

    /**
     * PUBLIC_INTERFACE
     *
     * <p>Redirects {@code /api-docs} to {@code /v3/api-docs}. This keeps compatibility with older
     * configuration and bookmarks.
     *
     * @param request incoming HTTP request (used to preserve forwarded host/scheme/port)
     * @return redirect response to the canonical OpenAPI endpoint
     */
    @GetMapping("/api-docs")
    public RedirectView apiDocs(HttpServletRequest request) {
        return redirectToV3ApiDocs(request);
    }

    private RedirectView redirectToV3ApiDocs(HttpServletRequest request) {
        String target =
                UriComponentsBuilder.fromHttpRequest(new ServletServerHttpRequest(request))
                        .replacePath("/v3/api-docs")
                        .replaceQuery(null)
                        .build()
                        .toUriString();

        RedirectView rv = new RedirectView(target);
        rv.setHttp10Compatible(false);
        return rv;
    }
}
