package com.java.sadna.backend.sportshop.mapper.request.dto;

import com.java.sadna.backend.sportshop.api.generated.products.model.ProductUpdateRequest;
import com.java.sadna.backend.sportshop.mapper.BaseMapper;
import com.java.sadna.backend.sportshop.model.ProductUpdateRequestDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ProductUpdateRequestToProductUpdateRequestDtoMapper
        extends BaseMapper<ProductUpdateRequest, ProductUpdateRequestDto> {

    @Override
    @Mapping(target = "multiSize", expression = "java(Boolean.TRUE.equals(source.getIsMultiSize()))")
    ProductUpdateRequestDto map(ProductUpdateRequest source);
}
