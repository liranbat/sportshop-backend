package com.java.sadna.backend.sportshop.mapper;

import com.java.sadna.backend.sportshop.api.generated.products.model.Product;
import com.java.sadna.backend.sportshop.api.generated.products.model.ProductPage;
import com.java.sadna.backend.sportshop.model.PagedResult;
import com.java.sadna.backend.sportshop.model.ProductDto;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class PagedProductDtoToProductPageMapper
        implements BaseMapper<PagedResult<ProductDto>, ProductPage> {

    private final ProductDtoToProductMapper productDtoToProductMapper;

    public PagedProductDtoToProductPageMapper(ProductDtoToProductMapper productDtoToProductMapper) {
        this.productDtoToProductMapper = productDtoToProductMapper;
    }

    @Override
    public ProductPage map(PagedResult<ProductDto> source) {
        List<Product> items = source.getItems().stream()
                .map(productDtoToProductMapper::map)
                .toList();
        return new ProductPage(
                items,
                source.getPage(),
                source.getPageSize(),
                source.getTotalElements(),
                source.getTotalPages()
        );
    }
}
