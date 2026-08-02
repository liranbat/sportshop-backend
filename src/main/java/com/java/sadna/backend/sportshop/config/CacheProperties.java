package com.java.sadna.backend.sportshop.config;

import jakarta.validation.constraints.NotNull;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import java.time.Duration;

@ConfigurationProperties(prefix = "app.cache")
@Validated
public class CacheProperties {

    private final Duration categoriesTtl;

    public CacheProperties(@NotNull Duration categoriesTtl) {
        this.categoriesTtl = categoriesTtl;
    }

    public Duration getCategoriesTtl() {
        return categoriesTtl;
    }
}
