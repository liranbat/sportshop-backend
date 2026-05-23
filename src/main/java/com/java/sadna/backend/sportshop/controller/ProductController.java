package com.java.sadna.backend.sportshop.controller;

import com.java.sadna.backend.sportshop.api.generated.products.api.ProductsApi;
import com.java.sadna.backend.sportshop.api.generated.products.model.ProductDetail;
import com.java.sadna.backend.sportshop.api.generated.products.model.ProductPage;
import com.java.sadna.backend.sportshop.mapper.PagedProductDtoToProductPageMapper;
import com.java.sadna.backend.sportshop.mapper.ProductDetailDtoToProductDetailMapper;
import com.java.sadna.backend.sportshop.model.PagedResult;
import com.java.sadna.backend.sportshop.model.ProductDetailDto;
import com.java.sadna.backend.sportshop.model.ProductDto;
import com.java.sadna.backend.sportshop.service.ProductService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.List;

@RestController
public class ProductController implements ProductsApi {

    private final ProductService productService;
    private final PagedProductDtoToProductPageMapper pagedProductDtoToProductPageMapper;
    private final ProductDetailDtoToProductDetailMapper productDetailDtoToProductDetailMapper;

    public ProductController(ProductService productService,
                             PagedProductDtoToProductPageMapper pagedProductDtoToProductPageMapper,
                             ProductDetailDtoToProductDetailMapper productDetailDtoToProductDetailMapper) {
        this.productService = productService;
        this.pagedProductDtoToProductPageMapper = pagedProductDtoToProductPageMapper;
        this.productDetailDtoToProductDetailMapper = productDetailDtoToProductDetailMapper;
    }

    @Override
    public ResponseEntity<ProductPage> listProducts(Boolean active,
                                                    String search,
                                                    List<Long> categoryIds,
                                                    BigDecimal priceMin,
                                                    BigDecimal priceMax,
                                                    String sortField,
                                                    String sortDirection,
                                                    Integer page,
                                                    Integer pageSize) {
        PagedResult<ProductDto> result = productService.list(
                active, search, categoryIds, priceMin, priceMax,
                sortField, sortDirection, page, pageSize
        );
        return ResponseEntity.ok(pagedProductDtoToProductPageMapper.map(result));
    }

    @Override
    public ResponseEntity<ProductDetail> getProduct(Long id) {
        ProductDetailDto detail = productService.getById(id);
        return ResponseEntity.ok(productDetailDtoToProductDetailMapper.map(detail));
    }
}
