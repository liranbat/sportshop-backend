package com.java.sadna.backend.sportshop.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import org.hibernate.annotations.Formula;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Entity
@Table(name = "orders")
public class OrderEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    private UserEntity user;

    @Column(name = "status", nullable = false, length = 32)
    private String status;

    @Column(name = "total_price", nullable = false, precision = 10, scale = 2)
    private BigDecimal totalPrice;

    @Column(name = "order_number", nullable = false, length = 23, unique = true)
    private String orderNumber;

    @Column(name = "created_at", nullable = false, insertable = false, updatable = false)
    private OffsetDateTime createdAt;

    @Column(name = "updated_at", insertable = false, updatable = false)
    private OffsetDateTime updatedAt;

    @Column(name = "updated_by", insertable = false, updatable = false)
    private Long updatedBy;

    @Column(name = "cancelled_at", insertable = false, updatable = false)
    private OffsetDateTime cancelledAt;

    @Column(name = "cancelled_by", insertable = false, updatable = false)
    private Long cancelledBy;

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

    @Formula("(SELECT COUNT(*) FROM order_items oi WHERE oi.order_id = id)")
    private int itemCount;

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

    public UserEntity getUser() {
        return user;
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

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public OffsetDateTime getUpdatedAt() {
        return updatedAt;
    }

    public Long getUpdatedBy() {
        return updatedBy;
    }

    public OffsetDateTime getCancelledAt() {
        return cancelledAt;
    }

    public Long getCancelledBy() {
        return cancelledBy;
    }

    public int getItemCount() {
        return itemCount;
    }
}
