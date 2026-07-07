package com.java.sadna.backend.sportshop.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Getter
@RequiredArgsConstructor
public class OrderPaymentDto {

    private final String provider;
    private final String transactionId;
    private final BigDecimal amount;
    private final String currency;
    private final String status;
    private final OffsetDateTime processedAt;
    private final OffsetDateTime refundedAt;
}
