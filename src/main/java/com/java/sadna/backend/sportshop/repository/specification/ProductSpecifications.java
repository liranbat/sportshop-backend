package com.java.sadna.backend.sportshop.repository.specification;

import com.java.sadna.backend.sportshop.common.constants.ProductConstants;
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
        return (root, query, cb) -> cb.equal(root.get(ProductConstants.ARCHIVED), !active);
    }

    public static Specification<ProductEntity> isMultiSize(Boolean isMultiSize) {
        return SpecsUtil.equal(ProductConstants.MULTI_SIZE, isMultiSize);
    }

    public static Specification<ProductEntity> nameContainsIgnoreCase(String search) {
        return SpecsUtil.likeContainsIgnoreCase(ProductConstants.NAME, search);
    }

    public static Specification<ProductEntity> categoryIdIn(Collection<Long> categoryIds) {
        return SpecsUtil.in(ProductConstants.CATEGORY_ID, categoryIds);
    }

    public static Specification<ProductEntity> priceGte(BigDecimal priceMin) {
        if (priceMin == null) return null;
        return (root, query, cb) -> cb.greaterThanOrEqualTo(root.get(ProductConstants.PRICE), priceMin);
    }

    public static Specification<ProductEntity> priceLte(BigDecimal priceMax) {
        if (priceMax == null) return null;
        return (root, query, cb) -> cb.lessThanOrEqualTo(root.get(ProductConstants.PRICE), priceMax);
    }
}
