package com.java.sadna.backend.sportshop.mapper.dto.response;

import com.java.sadna.backend.sportshop.api.generated.products.model.ProductSize;
import com.java.sadna.backend.sportshop.mapper.BaseMapper;
import com.java.sadna.backend.sportshop.model.ProductSizeDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ProductSizeDtoToProductSizeMapper extends BaseMapper<ProductSizeDto, ProductSize> {

    @Override
    ProductSize map(ProductSizeDto source);
}
