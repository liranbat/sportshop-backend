package com.java.sadna.backend.sportshop.repository.specification;

import com.java.sadna.backend.sportshop.entity.OrderEntity;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

public final class OrderSpecifications {

    private OrderSpecifications() {
    }

    public static Specification<OrderEntity> userIdEquals(Long userId) {
        if (userId == null) return null;
        return (root, query, cb) -> cb.equal(root.get("userId"), userId);
    }

    public static Specification<OrderEntity> statusEquals(String status) {
        if (status == null) return null;
        return (root, query, cb) -> cb.equal(root.get("status"), status);
    }

    public static Specification<OrderEntity> orderNumberContainsIgnoreCase(String search) {
        if (search == null) return null;
        String trimmed = search.trim();
        if (trimmed.isEmpty()) return null;
        String pattern = "%" + trimmed.toLowerCase() + "%";
        return (root, query, cb) -> cb.like(cb.lower(root.get("orderNumber")), pattern);
    }

    public static Specification<OrderEntity> totalPriceGte(BigDecimal min) {
        if (min == null) return null;
        return (root, query, cb) -> cb.greaterThanOrEqualTo(root.get("totalPrice"), min);
    }

    public static Specification<OrderEntity> totalPriceLte(BigDecimal max) {
        if (max == null) return null;
        return (root, query, cb) -> cb.lessThanOrEqualTo(root.get("totalPrice"), max);
    }

    public static Specification<OrderEntity> createdAtGte(OffsetDateTime fromInclusive) {
        if (fromInclusive == null) return null;
        return (root, query, cb) -> cb.greaterThanOrEqualTo(root.get("createdAt"), fromInclusive);
    }

    public static Specification<OrderEntity> createdAtLt(OffsetDateTime toExclusive) {
        if (toExclusive == null) return null;
        return (root, query, cb) -> cb.lessThan(root.get("createdAt"), toExclusive);
    }
}
