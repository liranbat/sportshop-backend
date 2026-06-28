package com.java.sadna.backend.sportshop.model;

public class StockRowDto {

    private final Long productId;
    private final String productName;
    private final String productImageFilename;
    private final boolean productIsArchived;
    private final boolean productIsMultiSize;
    private final String size;
    private final int quantity;
    private final Integer lowStockThreshold;

    public StockRowDto(Long productId,
                       String productName,
                       String productImageFilename,
                       boolean productIsArchived,
                       boolean productIsMultiSize,
                       String size,
                       int quantity,
                       Integer lowStockThreshold) {
        this.productId = productId;
        this.productName = productName;
        this.productImageFilename = productImageFilename;
        this.productIsArchived = productIsArchived;
        this.productIsMultiSize = productIsMultiSize;
        this.size = size;
        this.quantity = quantity;
        this.lowStockThreshold = lowStockThreshold;
    }

    public Long getProductId() {
        return productId;
    }

    public String getProductName() {
        return productName;
    }

    public String getProductImageFilename() {
        return productImageFilename;
    }

    public boolean isProductIsArchived() {
        return productIsArchived;
    }

    public boolean isProductIsMultiSize() {
        return productIsMultiSize;
    }

    public String getSize() {
        return size;
    }

    public int getQuantity() {
        return quantity;
    }

    public Integer getLowStockThreshold() {
        return lowStockThreshold;
    }
}
