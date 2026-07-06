package com.java.sadna.backend.sportshop.config;

import jakarta.validation.constraints.Min;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;
import org.springframework.validation.annotation.Validated;

@ConfigurationProperties(prefix = "app.checkout")
@Validated
public class CheckoutProperties {

    private final int orderNumberRetries;

    public CheckoutProperties(
            @DefaultValue("5") @Min(1) int orderNumberRetries) {
        this.orderNumberRetries = orderNumberRetries;
    }

    public int getOrderNumberRetries() {
        return orderNumberRetries;
    }
}
