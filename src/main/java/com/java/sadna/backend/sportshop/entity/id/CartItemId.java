package com.java.sadna.backend.sportshop.entity.id;

import java.io.Serializable;
import java.util.Objects;

// JPA composite-key class for CartItemEntity (user_id, product_id, size). Required by
// @IdClass: a no-arg constructor + value-based equals/hashCode + Serializable.
public class CartItemId implements Serializable {

    private Long userId;
    private Long productId;
    private String size;

    public CartItemId() {
    }

    public CartItemId(Long userId, Long productId, String size) {
        this.userId = userId;
        this.productId = productId;
        this.size = size;
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

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof CartItemId that))
            return false;
        return Objects.equals(userId, that.userId)
                && Objects.equals(productId, that.productId)
                && Objects.equals(size, that.size);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, productId, size);
    }
}
