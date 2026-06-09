package com.java.sadna.backend.sportshop.entity.id;

import java.io.Serializable;
import java.util.Objects;

// JPA composite-key class for ProductStockEntity (product_id, size). Required by
// @IdClass: a no-arg constructor + value-based equals/hashCode + Serializable.
public class ProductStockId implements Serializable {

    private Long productId;
    private String size;

    public ProductStockId() {
    }

    public ProductStockId(Long productId, String size) {
        this.productId = productId;
        this.size = size;
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
        if (!(o instanceof ProductStockId that)) 
            return false;
        return Objects.equals(productId, that.productId) && Objects.equals(size, that.size);
    }

    @Override
    public int hashCode() {
        return Objects.hash(productId, size);
    }
}
