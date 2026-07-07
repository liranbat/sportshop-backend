package com.java.sadna.backend.sportshop.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;

@Getter
@RequiredArgsConstructor
public class CartItemDto {

    private final Long productId;
    private final String size;
    private final int quantity;
    private final String productName;
    private final String productImageFilename;
    private final BigDecimal productPrice;
    private final String productCategoryName;
    private final boolean productIsArchived;
    private final int productVersionInCart;
    private final int productVersionCurrent;
    private final int availableStock;
    private final Integer lowStockThreshold;
    private final BigDecimal lineTotal;
}
