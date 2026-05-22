package com.java.sadna.backend.sportshop.mapper;

import com.java.sadna.backend.sportshop.entity.ProductEntity;
import com.java.sadna.backend.sportshop.model.ProductDto;
import org.springframework.stereotype.Component;

@Component
public class ProductEntityToProductDtoMapper implements BaseMapper<ProductEntity, ProductDto> {

    @Override
    public ProductDto map(ProductEntity productEntity) {
        return new ProductDto(
                productEntity.getId(),
                productEntity.getName(),
                productEntity.getDescription(),
                productEntity.getCategoryId(),
                productEntity.isMultiSize(),
                productEntity.getImageFilename(),
                productEntity.getPrice(),
                productEntity.getVersion()
        );
    }
}
