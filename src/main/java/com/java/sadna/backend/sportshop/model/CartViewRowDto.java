package com.java.sadna.backend.sportshop.model;

import java.math.BigDecimal;

public class CartViewRowDto implements BaseDto {

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

    public CartViewRowDto(Long productId,
                          String size,
                          Integer quantity,
                          Integer productVersionInCart,
                          Long productPk,
                          String productName,
                          String productImageFilename,
                          BigDecimal productPrice,
                          Boolean productArchived,
                          Integer productVersionCurrent,
                          String categoryName,
                          Integer availableStock,
                          Integer lowStockThreshold) {
        this.productId = productId;
        this.size = size;
        this.quantity = quantity;
        this.productVersionInCart = productVersionInCart;
        this.productPk = productPk;
        this.productName = productName;
        this.productImageFilename = productImageFilename;
        this.productPrice = productPrice;
        this.productArchived = productArchived;
        this.productVersionCurrent = productVersionCurrent;
        this.categoryName = categoryName;
        this.availableStock = availableStock;
        this.lowStockThreshold = lowStockThreshold;
    }

    public Long getProductId() {
        return productId;
    }

    public String getSize() {
        return size;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public Integer getProductVersionInCart() {
        return productVersionInCart;
    }

    public Long getProductPk() {
        return productPk;
    }

    public String getProductName() {
        return productName;
    }

    public String getProductImageFilename() {
        return productImageFilename;
    }

    public BigDecimal getProductPrice() {
        return productPrice;
    }

    public Boolean getProductArchived() {
        return productArchived;
    }

    public Integer getProductVersionCurrent() {
        return productVersionCurrent;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public Integer getAvailableStock() {
        return availableStock;
    }

    public Integer getLowStockThreshold() {
        return lowStockThreshold;
    }
}
