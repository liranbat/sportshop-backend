package com.java.sadna.backend.sportshop.mapper;

import com.java.sadna.backend.sportshop.entity.CategoryEntity;
import com.java.sadna.backend.sportshop.model.CategoryDto;
import org.springframework.stereotype.Component;

@Component
public class CategoryEntityToCategoryDtoMapper implements BaseMapper<CategoryEntity, CategoryDto> {

    @Override
    public CategoryDto map(CategoryEntity entity) {
        return new CategoryDto(
                entity.getId(),
                entity.getName(),
                entity.getIconFilename(),
                entity.isDeleted(),
                entity.getCreatedAt(),
                entity.getDeletedAt(),
                entity.getDeletedBy(),
                entity.getUpdatedAt(),
                entity.getUpdatedBy()
        );
    }
}
