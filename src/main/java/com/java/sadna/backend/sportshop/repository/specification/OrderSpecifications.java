package com.java.sadna.backend.sportshop.repository.specification;

import com.java.sadna.backend.sportshop.common.constants.OrderConstants;
import com.java.sadna.backend.sportshop.common.util.SpecsUtil;
import com.java.sadna.backend.sportshop.entity.OrderEntity;
import com.java.sadna.backend.sportshop.entity.UserEntity;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

public final class OrderSpecifications {

    private OrderSpecifications() {
    }

    public static Specification<OrderEntity> userIdEquals(Long userId) {
        return SpecsUtil.equal(OrderConstants.USER_ID, userId);
    }

    public static Specification<OrderEntity> statusEquals(String status) {
        return SpecsUtil.equal(OrderConstants.STATUS, status);
    }

    public static Specification<OrderEntity> orderNumberContainsIgnoreCase(String search) {
        return SpecsUtil.likeContainsIgnoreCase(OrderConstants.ORDER_NUMBER, search);
    }

    public static Specification<OrderEntity> totalPriceGte(BigDecimal min) {
        if (min == null) return null;
        return (root, query, cb) -> cb.greaterThanOrEqualTo(root.get(OrderConstants.TOTAL_PRICE), min);
    }

    public static Specification<OrderEntity> totalPriceLte(BigDecimal max) {
        if (max == null) return null;
        return (root, query, cb) -> cb.lessThanOrEqualTo(root.get(OrderConstants.TOTAL_PRICE), max);
    }

    public static Specification<OrderEntity> createdAtGte(OffsetDateTime fromInclusive) {
        if (fromInclusive == null) return null;
        return (root, query, cb) -> cb.greaterThanOrEqualTo(root.get(OrderConstants.CREATED_AT), fromInclusive);
    }

    public static Specification<OrderEntity> createdAtLt(OffsetDateTime toExclusive) {
        if (toExclusive == null) return null;
        return (root, query, cb) -> cb.lessThan(root.get(OrderConstants.CREATED_AT), toExclusive);
    }

    public static Specification<OrderEntity> customerMatches(String customer) {
        String pattern = SpecsUtil.toContainsPattern(customer);
        if (pattern == null) return null;
        return (root, query, cb) -> {
            Join<OrderEntity, UserEntity> user = root.join(OrderConstants.USER, JoinType.INNER);
            return SpecsUtil.fullNameOrEmailLike(cb, user, pattern);
        };
    }
}
