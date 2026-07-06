package com.java.sadna.backend.sportshop.mapper.dto.response;

import com.java.sadna.backend.sportshop.api.generated.products.model.ProductPage;
import com.java.sadna.backend.sportshop.mapper.BaseMapper;
import com.java.sadna.backend.sportshop.model.PagedResult;
import com.java.sadna.backend.sportshop.model.ProductDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = ProductDtoToProductMapper.class)
public interface PagedProductDtoToProductPageMapper
        extends BaseMapper<PagedResult<ProductDto>, ProductPage> {

    @Override
    ProductPage map(PagedResult<ProductDto> source);
}
