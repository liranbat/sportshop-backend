package com.java.sadna.backend.sportshop.mapper;

import com.java.sadna.backend.sportshop.api.generated.categories.model.Category;
import com.java.sadna.backend.sportshop.config.ImagesProperties;
import com.java.sadna.backend.sportshop.model.CategoryDto;
import org.springframework.stereotype.Component;

@Component
public class CategoryDtoToCategoryMapper implements BaseMapper<CategoryDto, Category> {

    private final ImagesProperties imagesProperties;

    public CategoryDtoToCategoryMapper(ImagesProperties imagesProperties) {
        this.imagesProperties = imagesProperties;
    }

    @Override
    public Category map(CategoryDto dto) {
        return new Category(
                dto.getId(),
                dto.getName(),
                imagesProperties.getCategoryIconUrl(dto.getIconFilename()),
                dto.isDeleted(),
                dto.getCreatedAt()
        )
                .deletedAt(dto.getDeletedAt())
                .deletedBy(dto.getDeletedBy())
                .updatedAt(dto.getUpdatedAt())
                .updatedBy(dto.getUpdatedBy());
    }
}
