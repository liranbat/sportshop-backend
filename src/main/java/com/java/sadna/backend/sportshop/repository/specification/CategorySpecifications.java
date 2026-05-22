package com.java.sadna.backend.sportshop.repository.specification;

import com.java.sadna.backend.sportshop.entity.CategoryEntity;
import org.springframework.data.jpa.domain.Specification;

public final class CategorySpecifications {

    private CategorySpecifications() {
    }

    public static Specification<CategoryEntity> active(Boolean active) {
        if (active == null) return null;
        return (root, query, cb) -> cb.equal(root.get("deleted"), !active);
    }
}
