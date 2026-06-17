package com.java.sadna.backend.sportshop.repository.specification;

import com.java.sadna.backend.sportshop.entity.OrderEntity;
import com.java.sadna.backend.sportshop.entity.UserEntity;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Path;
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

    public static Specification<OrderEntity> customerMatches(String customer) {
        if (customer == null) return null;
        String trimmed = customer.trim();
        if (trimmed.isEmpty()) return null;
        String pattern = "%" + trimmed.toLowerCase() + "%";
        return (root, query, cb) -> {
            Join<OrderEntity, UserEntity> user = root.join("user", JoinType.INNER);
            Path<String> firstName = user.get("firstName");
            Path<String> lastName = user.get("lastName");
            Expression<String> firstSpaceLast = cb.lower(cb.concat(cb.concat(firstName, " "), lastName));
            Expression<String> lastSpaceFirst = cb.lower(cb.concat(cb.concat(lastName, " "), firstName));
            return cb.or(
                    cb.like(cb.lower(firstName), pattern),
                    cb.like(cb.lower(lastName), pattern),
                    cb.like(cb.lower(user.get("email")), pattern),
                    cb.like(firstSpaceLast, pattern),
                    cb.like(lastSpaceFirst, pattern)
            );
        };
    }
}
