package com.java.sadna.backend.sportshop.controller;

import com.java.sadna.backend.sportshop.api.generated.products.api.AdminProductsApi;
import com.java.sadna.backend.sportshop.api.generated.products.api.ProductsApi;
import com.java.sadna.backend.sportshop.api.generated.products.model.ProductArchiveStatusFilter;
import com.java.sadna.backend.sportshop.api.generated.products.model.ProductCreateRequest;
import com.java.sadna.backend.sportshop.api.generated.products.model.ProductDetail;
import com.java.sadna.backend.sportshop.api.generated.products.model.ProductPage;
import com.java.sadna.backend.sportshop.mapper.PagedProductDtoToProductPageMapper;
import com.java.sadna.backend.sportshop.mapper.ProductCreateRequestToProductCreateRequestDtoMapper;
import com.java.sadna.backend.sportshop.mapper.ProductDetailDtoToProductDetailMapper;
import com.java.sadna.backend.sportshop.model.PagedResult;
import com.java.sadna.backend.sportshop.model.ProductDetailDto;
import com.java.sadna.backend.sportshop.model.ProductDto;
import com.java.sadna.backend.sportshop.service.ProductService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.List;

@RestController
public class ProductController implements ProductsApi, AdminProductsApi {

    private final ProductService productService;
    private final PagedProductDtoToProductPageMapper pagedProductDtoToProductPageMapper;
    private final ProductDetailDtoToProductDetailMapper productDetailDtoToProductDetailMapper;
    private final ProductCreateRequestToProductCreateRequestDtoMapper productCreateRequestToProductCreateRequestDtoMapper;

    public ProductController(ProductService productService,
                             PagedProductDtoToProductPageMapper pagedProductDtoToProductPageMapper,
                             ProductDetailDtoToProductDetailMapper productDetailDtoToProductDetailMapper,
                             ProductCreateRequestToProductCreateRequestDtoMapper productCreateRequestToProductCreateRequestDtoMapper) {
        this.productService = productService;
        this.pagedProductDtoToProductPageMapper = pagedProductDtoToProductPageMapper;
        this.productDetailDtoToProductDetailMapper = productDetailDtoToProductDetailMapper;
        this.productCreateRequestToProductCreateRequestDtoMapper = productCreateRequestToProductCreateRequestDtoMapper;
    }

    @Override
    public ResponseEntity<ProductPage> listProducts(String search,
                                                    List<Long> categoryIds,
                                                    BigDecimal priceMin,
                                                    BigDecimal priceMax,
                                                    String sortField,
                                                    String sortDirection,
                                                    Integer page,
                                                    Integer pageSize) {
        PagedResult<ProductDto> result = productService.list(
                Boolean.TRUE, search, categoryIds, priceMin, priceMax,
                sortField, sortDirection, page, pageSize
        );
        return ResponseEntity.ok(pagedProductDtoToProductPageMapper.map(result));
    }

    @Override
    public ResponseEntity<ProductDetail> getProduct(Long id) {
        ProductDetailDto detail = productService.getById(id);
        return ResponseEntity.ok(productDetailDtoToProductDetailMapper.map(detail));
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ProductPage> listAdminProducts(ProductArchiveStatusFilter archiveStatus,
                                                         String search,
                                                         List<Long> categoryIds,
                                                         BigDecimal priceMin,
                                                         BigDecimal priceMax,
                                                         String sortField,
                                                         String sortDirection,
                                                         Integer page,
                                                         Integer pageSize) {
        Boolean active = toActive(archiveStatus);
        PagedResult<ProductDto> result = productService.list(
                active, search, categoryIds, priceMin, priceMax,
                sortField, sortDirection, page, pageSize
        );
        return ResponseEntity.ok(pagedProductDtoToProductPageMapper.map(result));
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ProductDetail> createAdminProduct(ProductCreateRequest req) {
        ProductDetailDto detail = productService.create(
                productCreateRequestToProductCreateRequestDtoMapper.map(req)
        );
        return ResponseEntity.ok(productDetailDtoToProductDetailMapper.map(detail));
    }

    private static Boolean toActive(ProductArchiveStatusFilter archiveStatus) {
        if (archiveStatus == null) return null;
        return switch (archiveStatus) {
            case ACTIVE -> Boolean.TRUE;
            case ARCHIVED -> Boolean.FALSE;
        };
    }
}
