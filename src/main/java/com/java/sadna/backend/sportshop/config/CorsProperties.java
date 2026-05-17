package com.java.sadna.backend.sportshop.config;

import java.util.List;

public class CorsProperties {

    private final List<String> allowedOrigins;

    public CorsProperties(List<String> allowedOrigins) {
        this.allowedOrigins = allowedOrigins;
    }

    public List<String> getAllowedOrigins() {
        return allowedOrigins;
    }
}
