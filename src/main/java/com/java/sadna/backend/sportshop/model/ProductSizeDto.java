package com.java.sadna.backend.sportshop.model;

import com.java.sadna.backend.sportshop.model.enums.StockState;

public class ProductSizeDto implements BaseDto {

    private final String size;
    private final int quantity;
    private final StockState state;

    public ProductSizeDto(String size, int quantity, StockState state) {
        this.size = size;
        this.quantity = quantity;
        this.state = state;
    }

    public String getSize() {
        return size;
    }

    public int getQuantity() {
        return quantity;
    }

    public StockState getState() {
        return state;
    }
}
