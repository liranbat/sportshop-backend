package com.java.sadna.backend.sportshop.config;

import jakarta.validation.constraints.NotBlank;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@ConfigurationProperties(prefix = "app.api")
@Validated
public class ApiProperties {

    private final String pathPrefix;

    public ApiProperties(@NotBlank String pathPrefix) {
        this.pathPrefix = pathPrefix;
    }

    public String getPathPrefix() {
        return pathPrefix;
    }
}
