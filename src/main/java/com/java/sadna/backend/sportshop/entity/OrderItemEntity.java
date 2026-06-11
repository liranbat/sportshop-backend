package com.java.sadna.backend.sportshop.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.math.BigDecimal;

@Entity
@Table(name = "order_items")
public class OrderItemEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "order_id", nullable = false)
    private Long orderId;

    @Column(name = "product_id", nullable = false)
    private Long productId;

    @Column(name = "product_version", nullable = false)
    private int productVersion;

    @Column(name = "quantity", nullable = false)
    private int quantity;

    @Column(name = "price_per_unit", nullable = false, precision = 10, scale = 2)
    private BigDecimal pricePerUnit;

    @Column(name = "product_name", nullable = false, length = 200)
    private String productName;

    @Column(name = "product_image_url", length = 500)
    private String productImageUrl;

    @Column(name = "size", nullable = false, length = 20)
    private String size;

    protected OrderItemEntity() {
    }

    public OrderItemEntity(Long orderId,
                           Long productId,
                           int productVersion,
                           int quantity,
                           BigDecimal pricePerUnit,
                           String productName,
                           String productImageUrl,
                           String size) {
        this.orderId = orderId;
        this.productId = productId;
        this.productVersion = productVersion;
        this.quantity = quantity;
        this.pricePerUnit = pricePerUnit;
        this.productName = productName;
        this.productImageUrl = productImageUrl;
        this.size = size;
    }

    public Long getId() {
        return id;
    }

    public Long getOrderId() {
        return orderId;
    }

    public Long getProductId() {
        return productId;
    }

    public int getProductVersion() {
        return productVersion;
    }

    public int getQuantity() {
        return quantity;
    }

    public BigDecimal getPricePerUnit() {
        return pricePerUnit;
    }

    public String getProductName() {
        return productName;
    }

    public String getProductImageUrl() {
        return productImageUrl;
    }

    public String getSize() {
        return size;
    }
}
