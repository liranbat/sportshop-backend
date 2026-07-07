package com.java.sadna.backend.sportshop.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

// Stamps cache + image-specific security headers on every /images/** response.
// Registered (with URL pattern + order) in WebMvcConfig -- not a @Component, so
// it doesn't get auto-registered against /*.
//
// Cache: single max-age (default 7d, app.images.cache-ttl-seconds). Image changes
// propagate by URL turnover (new UUID = new URL), not cache invalidation.
//
// Security:
//   - Content-Disposition: inline (render in browser, don't download).
//   - /images/categories/** gets a strict CSP -- defense-in-depth on top of SvgSecurityScanner.
//   - nosniff comes from Spring Security's default headers (applied to every response).
public class ImageResponseHeadersFilter extends OncePerRequestFilter {

    private static final String CATEGORY_SVG_CSP = "default-src 'none'; style-src 'unsafe-inline'";

    private final ImagesProperties images;

    public ImageResponseHeadersFilter(ImagesProperties images) {
        this.images = images;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {
        response.setHeader(HttpHeaders.CACHE_CONTROL, "public, max-age=" + images.getCacheTtlSeconds());
        response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "inline");

        if (request.getRequestURI().startsWith(images.getCategoryPathPrefix())) {
            response.setHeader("Content-Security-Policy", CATEGORY_SVG_CSP);
        }

        filterChain.doFilter(request, response);
    }
}
