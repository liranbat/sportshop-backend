package com.java.sadna.backend.sportshop.mapper;

import com.java.sadna.backend.sportshop.api.generated.categories.model.Category;
import com.java.sadna.backend.sportshop.config.AppProperties;
import com.java.sadna.backend.sportshop.config.ImagesProperties;
import com.java.sadna.backend.sportshop.model.CategoryDto;
import org.springframework.stereotype.Component;

@Component
public class CategoryDtoToCategoryMapper implements BaseMapper<CategoryDto, Category> {

    private final ImagesProperties imagesProperties;

    public CategoryDtoToCategoryMapper(AppProperties appProperties) {
        this.imagesProperties = appProperties.getImages();
    }

    @Override
    public Category map(CategoryDto category) {
        return new Category(
                category.getId(),
                category.getName(),
                imagesProperties.getCategoryIconUrl(category.getIconFilename())
        );
    }
}
