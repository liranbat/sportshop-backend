package com.java.sadna.backend.sportshop.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;

@Getter
@RequiredArgsConstructor
public class CartViewRowDto {

    private final Long productId;
    private final String size;
    private final Integer quantity;
    private final Integer productVersionInCart;
    private final Long productPk;
    private final String productName;
    private final String productImageFilename;
    private final BigDecimal productPrice;
    private final Boolean productArchived;
    private final Integer productVersionCurrent;
    private final String categoryName;
    private final Integer availableStock;
    private final Integer lowStockThreshold;
}
