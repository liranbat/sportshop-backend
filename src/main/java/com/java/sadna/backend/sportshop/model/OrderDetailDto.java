package com.java.sadna.backend.sportshop.model;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;

public class OrderDetailDto {

    private final String orderNumber;
    private final String status;
    private final OffsetDateTime createdAt;
    private final OffsetDateTime cancelledAt;
    private final BigDecimal totalPrice;
    private final int itemCount;
    private final List<OrderItemDto> items;
    private final ShippingDetailsDto shipping;
    private final OrderPaymentDto payment;

    public OrderDetailDto(String orderNumber,
                          String status,
                          OffsetDateTime createdAt,
                          OffsetDateTime cancelledAt,
                          BigDecimal totalPrice,
                          int itemCount,
                          List<OrderItemDto> items,
                          ShippingDetailsDto shipping,
                          OrderPaymentDto payment) {
        this.orderNumber = orderNumber;
        this.status = status;
        this.createdAt = createdAt;
        this.cancelledAt = cancelledAt;
        this.totalPrice = totalPrice;
        this.itemCount = itemCount;
        this.items = items;
        this.shipping = shipping;
        this.payment = payment;
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

    public OffsetDateTime getCancelledAt() {
        return cancelledAt;
    }

    public BigDecimal getTotalPrice() {
        return totalPrice;
    }

    public int getItemCount() {
        return itemCount;
    }

    public List<OrderItemDto> getItems() {
        return items;
    }

    public ShippingDetailsDto getShipping() {
        return shipping;
    }

    public OrderPaymentDto getPayment() {
        return payment;
    }
}
