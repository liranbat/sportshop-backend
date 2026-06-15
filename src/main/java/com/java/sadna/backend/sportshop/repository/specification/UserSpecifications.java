package com.java.sadna.backend.sportshop.repository.specification;

import com.java.sadna.backend.sportshop.entity.UserEntity;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Path;
import org.springframework.data.jpa.domain.Specification;

public final class UserSpecifications {

    private UserSpecifications() {
    }

    public static Specification<UserEntity> isAdminEquals(Boolean isAdmin) {
        if (isAdmin == null) return null;
        return (root, query, cb) -> cb.equal(root.get("admin"), isAdmin);
    }

    public static Specification<UserEntity> isDeletedEquals(Boolean isDeleted) {
        if (isDeleted == null) return null;
        return (root, query, cb) -> cb.equal(root.get("deleted"), isDeleted);
    }

    // Case-insensitive substring match. The value is matched (a) directly against firstName /
    // lastName / email / phone and (b) against the concatenated "firstName lastName" /
    // "lastName firstName" so a query like "Liran Batson" finds the user even though the literal
    // substring lives in two columns. Trim + lowercase so callers don't have to.
    public static Specification<UserEntity> searchMatches(String q) {
        if (q == null) return null;
        String trimmed = q.trim();
        if (trimmed.isEmpty()) return null;
        String pattern = "%" + trimmed.toLowerCase() + "%";
        return (root, query, cb) -> {
            Path<String> firstName = root.get("firstName");
            Path<String> lastName = root.get("lastName");
            Expression<String> firstSpaceLast = cb.lower(cb.concat(cb.concat(firstName, " "), lastName));
            Expression<String> lastSpaceFirst = cb.lower(cb.concat(cb.concat(lastName, " "), firstName));
            return cb.or(
                    cb.like(cb.lower(firstName), pattern),
                    cb.like(cb.lower(lastName), pattern),
                    cb.like(cb.lower(root.get("email")), pattern),
                    cb.like(cb.lower(root.get("phone")), pattern),
                    cb.like(firstSpaceLast, pattern),
                    cb.like(lastSpaceFirst, pattern)
            );
        };
    }
}
