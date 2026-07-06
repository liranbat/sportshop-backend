package com.java.sadna.backend.sportshop.config;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;
import org.springframework.validation.annotation.Validated;

import java.util.Map;

@ConfigurationProperties(prefix = "app.pagination")
@Validated
public class PaginationProperties {

    private final Map<String, Integer> defaultPageSize;

    public PaginationProperties(
            @DefaultValue @NotNull Map<@NotNull String, @NotNull @Min(1) Integer> defaultPageSize) {
        this.defaultPageSize = defaultPageSize;
    }

    public Map<String, Integer> getDefaultPageSize() {
        return defaultPageSize;
    }
}
