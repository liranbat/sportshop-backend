package com.java.sadna.backend.sportshop.model;

import java.math.BigDecimal;
import java.util.List;

public class CartViewDto implements BaseDto {

    private final List<CartItemDto> items;
    private final int itemCount;
    private final BigDecimal subtotal;

    public CartViewDto(List<CartItemDto> items, int itemCount, BigDecimal subtotal) {
        this.items = items;
        this.itemCount = itemCount;
        this.subtotal = subtotal;
    }

    public List<CartItemDto> getItems() {
        return items;
    }

    public int getItemCount() {
        return itemCount;
    }

    public BigDecimal getSubtotal() {
        return subtotal;
    }
}
