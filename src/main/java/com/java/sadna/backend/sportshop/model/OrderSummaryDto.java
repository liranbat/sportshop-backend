package com.java.sadna.backend.sportshop.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Getter
@RequiredArgsConstructor
public class OrderSummaryDto {

    private final String orderNumber;
    private final String status;
    private final OffsetDateTime createdAt;
    private final int itemCount;
    private final BigDecimal totalPrice;
    private final CustomerForOrderDto customer;
}
