package com.java.sadna.backend.sportshop.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;

@Getter
@RequiredArgsConstructor
public class OrderItemDto {

    private final Long productId;
    private final String productName;
    private final String productImageUrl;
    private final String size;
    private final int quantity;
    private final BigDecimal pricePerUnit;
    private final BigDecimal lineTotal;
}
