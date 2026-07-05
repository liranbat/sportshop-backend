package com.java.sadna.backend.sportshop.repository.specification;

import com.java.sadna.backend.sportshop.common.util.SpecsUtil;
import com.java.sadna.backend.sportshop.entity.ProductEntity;
import com.java.sadna.backend.sportshop.entity.ProductStockEntity;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import org.springframework.data.jpa.domain.Specification;

import java.util.Collection;

public final class StockSpecifications {

    public static final String STATUS_IN_STOCK = "IN_STOCK";
    public static final String STATUS_LOW_STOCK = "LOW_STOCK";
    public static final String STATUS_OUT_OF_STOCK = "OUT_OF_STOCK";
    public static final String ARCHIVE_ACTIVE = "ACTIVE";
    public static final String ARCHIVE_ARCHIVED = "ARCHIVED";

    private StockSpecifications() {
    }

    public static Specification<ProductStockEntity> productNameContainsIgnoreCase(String search) {
        String pattern = SpecsUtil.toContainsPattern(search);
        if (pattern == null) return null;
        return (root, query, cb) -> {
            Join<ProductStockEntity, ProductEntity> product = root.join("product", JoinType.INNER);
            return cb.like(cb.lower(product.get("name")), pattern);
        };
    }

    public static Specification<ProductStockEntity> sizeIn(Collection<String> sizes) {
        return SpecsUtil.in("size", sizes);
    }

    // Threshold-aware stockStatus filter. LOW_STOCK uses the row's own low_stock_threshold;
    // a null / 0 threshold means "never alert" so the row sits in IN_STOCK (not LOW_STOCK).
    public static Specification<ProductStockEntity> stockStatus(String status) {
        if (status == null) return null;
        return (root, query, cb) -> switch (status) {
            case STATUS_OUT_OF_STOCK -> cb.equal(root.get("quantity"), 0);
            case STATUS_LOW_STOCK -> cb.and(
                    cb.greaterThan(root.get("quantity"), 0),
                    cb.isNotNull(root.get("lowStockThreshold")),
                    cb.greaterThan(root.<Integer>get("lowStockThreshold"), 0),
                    cb.lessThanOrEqualTo(root.get("quantity"), root.<Integer>get("lowStockThreshold"))
            );
            // IN_STOCK == quantity > 0 AND NOT(low-stock); the not-low-stock part is
            // (threshold IS NULL OR threshold <= 0 OR quantity > threshold).
            case STATUS_IN_STOCK -> cb.and(
                    cb.greaterThan(root.get("quantity"), 0),
                    cb.or(
                            cb.isNull(root.get("lowStockThreshold")),
                            cb.lessThanOrEqualTo(root.<Integer>get("lowStockThreshold"), 0),
                            cb.greaterThan(root.<Integer>get("quantity"), root.<Integer>get("lowStockThreshold"))
                    )
            );
            default -> cb.conjunction();
        };
    }

    public static Specification<ProductStockEntity> archiveStatus(String archive) {
        if (archive == null) return null;
        return (root, query, cb) -> {
            Join<ProductStockEntity, ProductEntity> product = root.join("product", JoinType.INNER);
            return switch (archive) {
                case ARCHIVE_ACTIVE -> cb.isFalse(product.get("archived"));
                case ARCHIVE_ARCHIVED -> cb.isTrue(product.get("archived"));
                default -> cb.conjunction();
            };
        };
    }
}
