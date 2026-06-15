package com.java.sadna.backend.sportshop.repository.specification;

import com.java.sadna.backend.sportshop.entity.RefreshTokenEntity;
import com.java.sadna.backend.sportshop.entity.UserEntity;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Path;
import org.springframework.data.jpa.domain.Specification;

public final class SessionSpecifications {

    private SessionSpecifications() {
    }

    public static Specification<RefreshTokenEntity> searchMatches(String q) {
        if (q == null) return null;
        String trimmed = q.trim();
        if (trimmed.isEmpty()) return null;
        String pattern = "%" + trimmed.toLowerCase() + "%";
        return (root, query, cb) -> {
            Join<RefreshTokenEntity, UserEntity> user = root.join("user", JoinType.INNER);
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
