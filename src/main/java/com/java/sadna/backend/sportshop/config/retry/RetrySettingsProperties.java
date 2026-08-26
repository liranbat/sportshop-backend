package com.java.sadna.backend.sportshop.config.retry;

import jakarta.validation.constraints.Min;
import org.springframework.boot.context.properties.bind.DefaultValue;

public class RetrySettingsProperties {

    private final int maxAttempts;
    private final long delayMs;

    public RetrySettingsProperties(
            @DefaultValue("3") @Min(1) int maxAttempts,
            @DefaultValue("200") @Min(0) long delayMs) {
        this.maxAttempts = maxAttempts;
        this.delayMs = delayMs;
    }

    public int getMaxAttempts() {
        return maxAttempts;
    }

    public long getDelayMs() {
        return delayMs;
    }
}
