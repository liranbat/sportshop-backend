package com.java.sadna.backend.sportshop.mapper;

import com.java.sadna.backend.sportshop.entity.CategoryEntity;
import com.java.sadna.backend.sportshop.model.Category;
import org.springframework.stereotype.Component;

@Component
public class CategoryEntityToCategoryMapper {

    public Category map(CategoryEntity categoryEntity) {
        return new Category(
                categoryEntity.getId(),
                categoryEntity.getName(),
                categoryEntity.getIconFilename()
        );
    }
}
