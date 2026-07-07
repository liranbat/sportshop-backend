package com.java.sadna.backend.sportshop.common.util;

import com.java.sadna.backend.sportshop.common.constants.UserConstants;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.From;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.Collection;

public final class SpecsUtil {

    private SpecsUtil() {
    }

    public static <E> Specification<E> equal(String attribute, Object value) {
        if (value == null) return null;
        return (root, query, cb) -> cb.equal(root.get(attribute), value);
    }

    public static <E> Specification<E> in(String attribute, Collection<?> values) {
        if (values == null || values.isEmpty()) return null;
        return (root, query, cb) -> root.get(attribute).in(values);
    }

    // Case-insensitive "contains" LIKE against a single string attribute on the root.
    public static <E> Specification<E> likeContainsIgnoreCase(String attribute, String search) {
        String pattern = toContainsPattern(search);
        if (pattern == null) return null;
        return (root, query, cb) -> cb.like(cb.lower(root.get(attribute)), pattern);
    }

    // OR-matches %pattern% against firstName, lastName, email, "first last", "last first"
    // on the given user path (root or join).
    public static Predicate fullNameOrEmailLike(CriteriaBuilder cb,
                                                From<?, ?> userPath,
                                                String pattern) {
        Path<String> firstName = userPath.get(UserConstants.FIRST_NAME);
        Path<String> lastName = userPath.get(UserConstants.LAST_NAME);
        Path<String> email = userPath.get(UserConstants.EMAIL);
        Expression<String> firstSpaceLast = cb.lower(cb.concat(cb.concat(firstName, " "), lastName));
        Expression<String> lastSpaceFirst = cb.lower(cb.concat(cb.concat(lastName, " "), firstName));
        return cb.or(
                cb.like(cb.lower(firstName), pattern),
                cb.like(cb.lower(lastName), pattern),
                cb.like(cb.lower(email), pattern),
                cb.like(firstSpaceLast, pattern),
                cb.like(lastSpaceFirst, pattern)
        );
    }

    // `%lower(trim)%` LIKE pattern, or null if input is null/blank.
    public static String toContainsPattern(String search) {
        if (search == null) return null;
        String trimmed = search.trim();
        if (trimmed.isEmpty()) return null;
        return "%" + trimmed.toLowerCase() + "%";
    }
}
