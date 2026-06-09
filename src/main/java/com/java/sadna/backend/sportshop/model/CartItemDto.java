package com.java.sadna.backend.sportshop.model;

import java.math.BigDecimal;

public class CartItemDto implements BaseDto {

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

    public CartItemDto(Long productId,
                       String size,
                       int quantity,
                       String productName,
                       String productImageFilename,
                       BigDecimal productPrice,
                       String productCategoryName,
                       boolean productIsArchived,
                       int productVersionInCart,
                       int productVersionCurrent,
                       int availableStock,
                       Integer lowStockThreshold,
                       BigDecimal lineTotal) {
        this.productId = productId;
        this.size = size;
        this.quantity = quantity;
        this.productName = productName;
        this.productImageFilename = productImageFilename;
        this.productPrice = productPrice;
        this.productCategoryName = productCategoryName;
        this.productIsArchived = productIsArchived;
        this.productVersionInCart = productVersionInCart;
        this.productVersionCurrent = productVersionCurrent;
        this.availableStock = availableStock;
        this.lowStockThreshold = lowStockThreshold;
        this.lineTotal = lineTotal;
    }

    public Long getProductId() {
        return productId;
    }

    public String getSize() {
        return size;
    }

    public int getQuantity() {
        return quantity;
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

    public String getProductCategoryName() {
        return productCategoryName;
    }

    public boolean isProductIsArchived() {
        return productIsArchived;
    }

    public int getProductVersionInCart() {
        return productVersionInCart;
    }

    public int getProductVersionCurrent() {
        return productVersionCurrent;
    }

    public int getAvailableStock() {
        return availableStock;
    }

    public Integer getLowStockThreshold() {
        return lowStockThreshold;
    }

    public BigDecimal getLineTotal() {
        return lineTotal;
    }
}
