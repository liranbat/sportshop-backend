package com.java.sadna.backend.sportshop.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;

@Getter
@RequiredArgsConstructor
public class OrderDetailDto {

    private final String orderNumber;
    private final String status;
    private final OffsetDateTime createdAt;
    private final OffsetDateTime cancelledAt;
    private final BigDecimal totalPrice;
    private final int itemCount;
    private final List<OrderItemDto> items;
    private final ShippingDetailsDto shipping;
    private final OrderPaymentDto payment;
    private final CustomerForOrderDto customer;
}
