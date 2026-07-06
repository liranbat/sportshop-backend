package com.java.sadna.backend.sportshop.config;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;
import org.springframework.validation.annotation.Validated;

@ConfigurationProperties(prefix = "app.payment")
@Validated
public class PaymentProperties {

    private final String provider;
    private final String currency;
    private final Mock mock;

    public PaymentProperties(
            @DefaultValue("MOCK_CARD") @NotBlank String provider,
            @DefaultValue("USD") @NotBlank String currency,
            @DefaultValue @Valid Mock mock) {
        this.provider = provider;
        this.currency = currency;
        this.mock = mock;
    }

    public String getProvider() {
        return provider;
    }

    public String getCurrency() {
        return currency;
    }

    public Mock getMock() {
        return mock;
    }

    public static class Mock {

        private final String declineSuffix;

        public Mock(
                @DefaultValue("0000") @NotBlank String declineSuffix) {
            this.declineSuffix = declineSuffix;
        }

        public String getDeclineSuffix() {
            return declineSuffix;
        }
    }
}
