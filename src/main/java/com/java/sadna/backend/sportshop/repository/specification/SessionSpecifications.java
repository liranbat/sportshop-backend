package com.java.sadna.backend.sportshop.repository.specification;

import com.java.sadna.backend.sportshop.common.util.SpecsUtil;
import com.java.sadna.backend.sportshop.entity.RefreshTokenEntity;
import com.java.sadna.backend.sportshop.entity.UserEntity;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import org.springframework.data.jpa.domain.Specification;

public final class SessionSpecifications {

    private SessionSpecifications() {
    }

    public static Specification<RefreshTokenEntity> searchMatches(String q) {
        String pattern = SpecsUtil.toContainsPattern(q);
        if (pattern == null) return null;
        return (root, query, cb) -> {
            Join<RefreshTokenEntity, UserEntity> user = root.join("user", JoinType.INNER);
            return SpecsUtil.fullNameOrEmailLike(cb, user, pattern);
        };
    }
}
