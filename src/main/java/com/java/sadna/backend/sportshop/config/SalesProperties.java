package com.java.sadna.backend.sportshop.config;

import jakarta.validation.constraints.Min;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;
import org.springframework.validation.annotation.Validated;

import java.time.Duration;

@ConfigurationProperties(prefix = "app.sales")
@Validated
public class SalesProperties {

    private final int topProductsLimit;
    private final Duration maxDateRange;

    public SalesProperties(
            @DefaultValue("5") @Min(1) int topProductsLimit,
            @DefaultValue("180d") Duration maxDateRange) {
        if (maxDateRange.compareTo(Duration.ofDays(1)) < 0) {
            throw new IllegalArgumentException("app.sales.max-date-range must be at least 1d");
        }
        // whole days only — hours/minutes/seconds are not allowed
        if (!maxDateRange.minusDays(maxDateRange.toDays()).isZero()) {
            throw new IllegalArgumentException("app.sales.max-date-range must be a whole number of days");
        }
        this.topProductsLimit = topProductsLimit;
        this.maxDateRange = maxDateRange;
    }

    public int getTopProductsLimit() {
        return topProductsLimit;
    }

    public Duration getMaxDateRange() {
        return maxDateRange;
    }
}
