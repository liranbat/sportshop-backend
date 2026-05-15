package com.java.sadna.backend.sportshop.mapper;

import com.java.sadna.backend.sportshop.api.generated.categories.model.CategoryDto;
import com.java.sadna.backend.sportshop.config.AppProperties;
import com.java.sadna.backend.sportshop.config.ImagesProperties;
import com.java.sadna.backend.sportshop.model.Category;
import org.springframework.stereotype.Component;

@Component
public class CategoryToCategoryDtoMapper {

    private final ImagesProperties imagesProperties;

    public CategoryToCategoryDtoMapper(AppProperties appProperties) {
        this.imagesProperties = appProperties.getImages();
    }

    public CategoryDto map(Category category) {
        return new CategoryDto(
                category.getId(),
                category.getName(),
                imagesProperties.getCategoryIconUrl(category.getIconFilename())
        );
    }
}
