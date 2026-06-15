package com.java.sadna.backend.sportshop.model;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

public class OrderSummaryDto implements BaseDto {

    private final String orderNumber;
    private final String status;
    private final OffsetDateTime createdAt;
    private final int itemCount;
    private final BigDecimal totalPrice;

    public OrderSummaryDto(String orderNumber,
                           String status,
                           OffsetDateTime createdAt,
                           int itemCount,
                           BigDecimal totalPrice) {
        this.orderNumber = orderNumber;
        this.status = status;
        this.createdAt = createdAt;
        this.itemCount = itemCount;
        this.totalPrice = totalPrice;
    }

    public String getOrderNumber() {
        return orderNumber;
    }

    public String getStatus() {
        return status;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public int getItemCount() {
        return itemCount;
    }

    public BigDecimal getTotalPrice() {
        return totalPrice;
    }
}
