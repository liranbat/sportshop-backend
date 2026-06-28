package com.java.sadna.backend.sportshop.repository.specification;

import com.java.sadna.backend.sportshop.entity.ProductEntity;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.util.Collection;

public final class ProductSpecifications {

    private ProductSpecifications() {
    }

    public static Specification<ProductEntity> active(Boolean active) {
        if (active == null) return null;
        return (root, query, cb) -> cb.equal(root.get("archived"), !active);
    }

    public static Specification<ProductEntity> isMultiSize(Boolean isMultiSize) {
        if (isMultiSize == null) return null;
        return (root, query, cb) -> cb.equal(root.get("multiSize"), isMultiSize);
    }

    public static Specification<ProductEntity> nameContainsIgnoreCase(String search) {
        if (search == null) return null;
        String trimmed = search.trim();
        if (trimmed.isEmpty()) return null;
        String pattern = "%" + trimmed.toLowerCase() + "%";
        return (root, query, cb) -> cb.like(cb.lower(root.get("name")), pattern);
    }

    public static Specification<ProductEntity> categoryIdIn(Collection<Long> categoryIds) {
        if (categoryIds == null || categoryIds.isEmpty()) return null;
        return (root, query, cb) -> root.get("categoryId").in(categoryIds);
    }

    public static Specification<ProductEntity> priceGte(BigDecimal priceMin) {
        if (priceMin == null) return null;
        return (root, query, cb) -> cb.greaterThanOrEqualTo(root.get("price"), priceMin);
    }

    public static Specification<ProductEntity> priceLte(BigDecimal priceMax) {
        if (priceMax == null) return null;
        return (root, query, cb) -> cb.lessThanOrEqualTo(root.get("price"), priceMax);
    }
}
