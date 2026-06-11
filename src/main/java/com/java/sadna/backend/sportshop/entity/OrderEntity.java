package com.java.sadna.backend.sportshop.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.math.BigDecimal;

// audit / lifecycle columns (created_at, updated_at, cancelled_at, updated_by) intentionally
// unmapped -- DB defaults fire on insert; admin / history guides add them when needed.
@Entity
@Table(name = "orders")
public class OrderEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "status", nullable = false, length = 32)
    private String status;

    @Column(name = "total_price", nullable = false, precision = 10, scale = 2)
    private BigDecimal totalPrice;

    @Column(name = "order_number", nullable = false, length = 23, unique = true)
    private String orderNumber;

    @Column(name = "shipping_full_name", nullable = false, length = 100)
    private String shippingFullName;

    @Column(name = "shipping_email", nullable = false, length = 254)
    private String shippingEmail;

    @Column(name = "shipping_phone", nullable = false, length = 20)
    private String shippingPhone;

    @Column(name = "shipping_country", nullable = false, length = 100)
    private String shippingCountry;

    @Column(name = "shipping_city", nullable = false, length = 100)
    private String shippingCity;

    @Column(name = "shipping_address_line", nullable = false, length = 200)
    private String shippingAddressLine;

    protected OrderEntity() {
    }

    public OrderEntity(Long userId,
                       String status,
                       BigDecimal totalPrice,
                       String orderNumber,
                       String shippingFullName,
                       String shippingEmail,
                       String shippingPhone,
                       String shippingCountry,
                       String shippingCity,
                       String shippingAddressLine) {
        this.userId = userId;
        this.status = status;
        this.totalPrice = totalPrice;
        this.orderNumber = orderNumber;
        this.shippingFullName = shippingFullName;
        this.shippingEmail = shippingEmail;
        this.shippingPhone = shippingPhone;
        this.shippingCountry = shippingCountry;
        this.shippingCity = shippingCity;
        this.shippingAddressLine = shippingAddressLine;
    }

    public Long getId() {
        return id;
    }

    public Long getUserId() {
        return userId;
    }

    public String getStatus() {
        return status;
    }

    public BigDecimal getTotalPrice() {
        return totalPrice;
    }

    public String getOrderNumber() {
        return orderNumber;
    }

    public String getShippingFullName() {
        return shippingFullName;
    }

    public String getShippingEmail() {
        return shippingEmail;
    }

    public String getShippingPhone() {
        return shippingPhone;
    }

    public String getShippingCountry() {
        return shippingCountry;
    }

    public String getShippingCity() {
        return shippingCity;
    }

    public String getShippingAddressLine() {
        return shippingAddressLine;
    }
}
