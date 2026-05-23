package com.java.sadna.backend.sportshop.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;

@Entity
@Table(name = "product_stock")
@IdClass(ProductStockId.class)
public class ProductStockEntity {

    @Id
    @Column(name = "product_id", nullable = false)
    private Long productId;

    @Id
    @Column(name = "size", nullable = false, length = 20)
    private String size;

    @Column(name = "quantity", nullable = false)
    private int quantity;

    @Column(name = "low_stock_threshold")
    private Integer lowStockThreshold;

    protected ProductStockEntity() {
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

    public Integer getLowStockThreshold() {
        return lowStockThreshold;
    }
}
