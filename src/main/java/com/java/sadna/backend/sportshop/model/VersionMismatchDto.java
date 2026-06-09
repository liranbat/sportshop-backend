package com.java.sadna.backend.sportshop.model;

public class VersionMismatchDto implements BaseDto {

    private final Long productId;
    private final String productName;
    private final String size;
    private final boolean productIsArchived;

    public VersionMismatchDto(Long productId, String productName, String size, boolean productIsArchived) {
        this.productId = productId;
        this.productName = productName;
        this.size = size;
        this.productIsArchived = productIsArchived;
    }

    public Long getProductId() {
        return productId;
    }

    public String getProductName() {
        return productName;
    }

    public String getSize() {
        return size;
    }

    public boolean isProductIsArchived() {
        return productIsArchived;
    }
}
