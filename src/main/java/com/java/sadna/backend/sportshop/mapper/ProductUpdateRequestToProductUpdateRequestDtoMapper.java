package com.java.sadna.backend.sportshop.mapper;

import com.java.sadna.backend.sportshop.api.generated.products.model.ProductUpdateRequest;
import com.java.sadna.backend.sportshop.model.ProductUpdateRequestDto;
import org.springframework.stereotype.Component;

@Component
public class ProductUpdateRequestToProductUpdateRequestDtoMapper
        implements BaseMapper<ProductUpdateRequest, ProductUpdateRequestDto> {

    @Override
    public ProductUpdateRequestDto map(ProductUpdateRequest source) {
        return new ProductUpdateRequestDto(
                source.getName(),
                source.getDescription(),
                source.getCategoryId(),
                Boolean.TRUE.equals(source.getIsMultiSize()),
                source.getImageUrl(),
                source.getPrice(),
                source.getVersion()
        );
    }
}
