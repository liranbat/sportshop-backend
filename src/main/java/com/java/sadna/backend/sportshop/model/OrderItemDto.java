package com.java.sadna.backend.sportshop.model;

import java.math.BigDecimal;

public class OrderItemDto {

    private final Long productId;
    private final String productName;
    private final String productImageUrl;
    private final String size;
    private final int quantity;
    private final BigDecimal pricePerUnit;
    private final BigDecimal lineTotal;

    public OrderItemDto(Long productId,
                        String productName,
                        String productImageUrl,
                        String size,
                        int quantity,
                        BigDecimal pricePerUnit,
                        BigDecimal lineTotal) {
        this.productId = productId;
        this.productName = productName;
        this.productImageUrl = productImageUrl;
        this.size = size;
        this.quantity = quantity;
        this.pricePerUnit = pricePerUnit;
        this.lineTotal = lineTotal;
    }

    public Long getProductId() {
        return productId;
    }

    public String getProductName() {
        return productName;
    }

    public String getProductImageUrl() {
        return productImageUrl;
    }

    public String getSize() {
        return size;
    }

    public int getQuantity() {
        return quantity;
    }

    public BigDecimal getPricePerUnit() {
        return pricePerUnit;
    }

    public BigDecimal getLineTotal() {
        return lineTotal;
    }
}
