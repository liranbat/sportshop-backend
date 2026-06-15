package com.java.sadna.backend.sportshop.model;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

public class OrderPaymentDto implements BaseDto {

    private final String provider;
    private final String transactionId;
    private final BigDecimal amount;
    private final String currency;
    private final String status;
    private final OffsetDateTime processedAt;
    private final OffsetDateTime refundedAt;

    public OrderPaymentDto(String provider,
                           String transactionId,
                           BigDecimal amount,
                           String currency,
                           String status,
                           OffsetDateTime processedAt,
                           OffsetDateTime refundedAt) {
        this.provider = provider;
        this.transactionId = transactionId;
        this.amount = amount;
        this.currency = currency;
        this.status = status;
        this.processedAt = processedAt;
        this.refundedAt = refundedAt;
    }

    public String getProvider() {
        return provider;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public String getCurrency() {
        return currency;
    }

    public String getStatus() {
        return status;
    }

    public OffsetDateTime getProcessedAt() {
        return processedAt;
    }

    public OffsetDateTime getRefundedAt() {
        return refundedAt;
    }
}
