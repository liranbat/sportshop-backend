package com.java.sadna.backend.sportshop.mapper;

import com.java.sadna.backend.sportshop.entity.CategoryEntity;
import com.java.sadna.backend.sportshop.model.CategoryDto;
import org.springframework.stereotype.Component;

@Component
public class CategoryEntityToCategoryDtoMapper implements BaseMapper<CategoryEntity, CategoryDto> {

    @Override
    public CategoryDto map(CategoryEntity categoryEntity) {
        return new CategoryDto(
                categoryEntity.getId(),
                categoryEntity.getName(),
                categoryEntity.getIconFilename()
        );
    }
}
