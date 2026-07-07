package com.java.sadna.backend.sportshop.mapper.entity.dto;

import com.java.sadna.backend.sportshop.entity.ProductEntity;
import com.java.sadna.backend.sportshop.mapper.BaseMapper;
import com.java.sadna.backend.sportshop.model.ProductDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ProductEntityToProductDtoMapper extends BaseMapper<ProductEntity, ProductDto> {

    @Override
    ProductDto map(ProductEntity productEntity);
}
