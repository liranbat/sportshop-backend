package com.java.sadna.backend.sportshop.model;

public class CartCountDto implements BaseDto {

    private final int itemCount;

    public CartCountDto(int itemCount) {
        this.itemCount = itemCount;
    }

    public int getItemCount() {
        return itemCount;
    }
}
