package com.java.sadna.backend.sportshop.repository.specification;

import com.java.sadna.backend.sportshop.common.util.SpecsUtil;
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
        return SpecsUtil.equal("multiSize", isMultiSize);
    }

    public static Specification<ProductEntity> nameContainsIgnoreCase(String search) {
        return SpecsUtil.likeContainsIgnoreCase("name", search);
    }

    public static Specification<ProductEntity> categoryIdIn(Collection<Long> categoryIds) {
        return SpecsUtil.in("categoryId", categoryIds);
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
