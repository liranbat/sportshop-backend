package com.java.sadna.backend.sportshop.entity;

import com.java.sadna.backend.sportshop.entity.id.CartItemId;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;

@Entity
@Table(name = "cart_items")
@IdClass(CartItemId.class)
public class CartItemEntity {

    @Id
    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Id
    @Column(name = "product_id", nullable = false)
    private Long productId;

    @Id
    @Column(name = "size", nullable = false, length = 20)
    private String size;

    @Column(name = "quantity", nullable = false)
    private int quantity;

    @Column(name = "product_version", nullable = false)
    private int productVersion;

    protected CartItemEntity() {
    }

    public Long getUserId() {
        return userId;
    }

    public Long getProductId() {
        return productId;
    }

    public String getSize() {
        return size;
    }

    public int getQuantity() {
        return quantity;
    }

    public int getProductVersion() {
        return productVersion;
    }
}
