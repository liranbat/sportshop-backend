package com.java.sadna.backend.sportshop.repository.specification;

import com.java.sadna.backend.sportshop.common.util.SpecsUtil;
import com.java.sadna.backend.sportshop.entity.UserEntity;
import org.springframework.data.jpa.domain.Specification;

public final class UserSpecifications {

    private UserSpecifications() {
    }

    public static Specification<UserEntity> isAdminEquals(Boolean isAdmin) {
        return SpecsUtil.equal("admin", isAdmin);
    }

    public static Specification<UserEntity> isDeletedEquals(Boolean isDeleted) {
        return SpecsUtil.equal("deleted", isDeleted);
    }

    public static Specification<UserEntity> searchMatches(String q) {
        String pattern = SpecsUtil.toContainsPattern(q);
        if (pattern == null) return null;
        return (root, query, cb) -> cb.or(
                SpecsUtil.fullNameOrEmailLike(cb, root, pattern),
                cb.like(cb.lower(root.get("phone")), pattern)
        );
    }
}
