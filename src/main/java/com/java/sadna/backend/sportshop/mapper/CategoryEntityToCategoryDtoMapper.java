package com.java.sadna.backend.sportshop.mapper;

import com.java.sadna.backend.sportshop.entity.CategoryEntity;
import com.java.sadna.backend.sportshop.model.CategoryDto;
import org.springframework.stereotype.Component;

@Component
public class CategoryEntityToCategoryDtoMapper {

    public CategoryDto map(CategoryEntity categoryEntity) {
        return new CategoryDto(
                categoryEntity.getId(),
                categoryEntity.getName(),
                categoryEntity.getIconFilename()
        );
    }
}
