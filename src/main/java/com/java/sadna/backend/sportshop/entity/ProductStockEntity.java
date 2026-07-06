package com.java.sadna.backend.sportshop.entity;

import com.java.sadna.backend.sportshop.entity.id.ProductStockId;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "product_stock")
@IdClass(ProductStockId.class)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
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

    // Read-only association for joins/projections; productId stays the sole FK writer.
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "product_id", insertable = false, updatable = false)
    private ProductEntity product;

    public ProductStockEntity(Long productId, String size, int quantity, Integer lowStockThreshold) {
        this.productId = productId;
        this.size = size;
        this.quantity = quantity;
        this.lowStockThreshold = lowStockThreshold;
    }
}
