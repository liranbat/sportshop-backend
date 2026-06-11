package com.java.sadna.backend.sportshop.model;

import java.math.BigDecimal;

public class CheckoutResultDto implements BaseDto {

    private final String orderNumber;
    private final int itemCount;
    private final BigDecimal totalPrice;

    public CheckoutResultDto(String orderNumber, int itemCount, BigDecimal totalPrice) {
        this.orderNumber = orderNumber;
        this.itemCount = itemCount;
        this.totalPrice = totalPrice;
    }

    public String getOrderNumber() {
        return orderNumber;
    }

    public int getItemCount() {
        return itemCount;
    }

    public BigDecimal getTotalPrice() {
        return totalPrice;
    }
}
