package com.java.sadna.backend.sportshop.config.retry;

import jakarta.validation.Valid;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import java.util.Map;

@ConfigurationProperties(prefix = "app")
@Validated
public class RetryProperties {

    @Valid
    private final Map<String, RetrySettingsProperties> retry;

    public RetryProperties(Map<String, RetrySettingsProperties> retry) {
        this.retry = Map.copyOf(retry);
    }

    public Map<String, RetrySettingsProperties> getRetry() {
        return retry;
    }

    public RetrySettingsProperties get(String name) {
        return retry.get(name);
    }
}
