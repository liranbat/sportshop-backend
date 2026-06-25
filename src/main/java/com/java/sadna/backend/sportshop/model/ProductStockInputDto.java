package com.java.sadna.backend.sportshop.model;

public class ProductStockInputDto implements BaseDto {

    private final int quantity;
    private final Integer lowStockThreshold;

    public ProductStockInputDto(int quantity, Integer lowStockThreshold) {
        this.quantity = quantity;
        this.lowStockThreshold = lowStockThreshold;
    }

    public int getQuantity() {
        return quantity;
    }

    public Integer getLowStockThreshold() {
        return lowStockThreshold;
    }
}
