package com.java.sadna.backend.sportshop.controller;

import com.java.sadna.backend.sportshop.api.generated.products.api.AdminProductsApi;
import com.java.sadna.backend.sportshop.api.generated.products.api.ProductsApi;
import com.java.sadna.backend.sportshop.api.generated.products.model.ProductArchiveStatusFilter;
import com.java.sadna.backend.sportshop.api.generated.products.model.ProductCreateRequest;
import com.java.sadna.backend.sportshop.api.generated.products.model.ProductDetail;
import com.java.sadna.backend.sportshop.api.generated.products.model.ProductLifecycleRequest;
import com.java.sadna.backend.sportshop.api.generated.products.model.ProductPage;
import com.java.sadna.backend.sportshop.api.generated.products.model.ProductUpdateRequest;
import com.java.sadna.backend.sportshop.exception.NotFoundException;
import com.java.sadna.backend.sportshop.mapper.dto.response.PagedProductDtoToProductPageMapper;
import com.java.sadna.backend.sportshop.mapper.request.dto.ProductCreateRequestToProductCreateRequestDtoMapper;
import com.java.sadna.backend.sportshop.mapper.request.dto.ProductUpdateRequestToProductUpdateRequestDtoMapper;
import com.java.sadna.backend.sportshop.mapper.dto.response.ProductDetailDtoToProductDetailMapper;
import com.java.sadna.backend.sportshop.model.PagedResult;
import com.java.sadna.backend.sportshop.model.ProductDetailDto;
import com.java.sadna.backend.sportshop.model.ProductDto;
import com.java.sadna.backend.sportshop.common.constants.AuthorityConstants;
import com.java.sadna.backend.sportshop.security.SecurityContextUtils;
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
    private final ProductUpdateRequestToProductUpdateRequestDtoMapper productUpdateRequestToProductUpdateRequestDtoMapper;

    public ProductController(ProductService productService,
                             PagedProductDtoToProductPageMapper pagedProductDtoToProductPageMapper,
                             ProductDetailDtoToProductDetailMapper productDetailDtoToProductDetailMapper,
                             ProductCreateRequestToProductCreateRequestDtoMapper productCreateRequestToProductCreateRequestDtoMapper,
                             ProductUpdateRequestToProductUpdateRequestDtoMapper productUpdateRequestToProductUpdateRequestDtoMapper) {
        this.productService = productService;
        this.pagedProductDtoToProductPageMapper = pagedProductDtoToProductPageMapper;
        this.productDetailDtoToProductDetailMapper = productDetailDtoToProductDetailMapper;
        this.productCreateRequestToProductCreateRequestDtoMapper = productCreateRequestToProductCreateRequestDtoMapper;
        this.productUpdateRequestToProductUpdateRequestDtoMapper = productUpdateRequestToProductUpdateRequestDtoMapper;
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
                Boolean.TRUE, null, search, categoryIds, priceMin, priceMax,
                sortField, sortDirection, page, pageSize
        );
        return ResponseEntity.ok(pagedProductDtoToProductPageMapper.map(result));
    }

    @Override
    public ResponseEntity<ProductDetail> getProduct(Long id) {
        ProductDetailDto detail = productService.getById(id);
        if (detail.getProduct().isArchived() && !SecurityContextUtils.currentUserIsAdmin()) {
            throw new NotFoundException("product.notFound", id);
        }
        return ResponseEntity.ok(productDetailDtoToProductDetailMapper.map(detail));
    }

    @Override
    @PreAuthorize(AuthorityConstants.ADMIN)
    public ResponseEntity<ProductPage> listAdminProducts(ProductArchiveStatusFilter archiveStatus,
                                                         Boolean isMultiSize,
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
                active, isMultiSize, search, categoryIds, priceMin, priceMax,
                sortField, sortDirection, page, pageSize
        );
        return ResponseEntity.ok(pagedProductDtoToProductPageMapper.map(result));
    }

    @Override
    @PreAuthorize(AuthorityConstants.ADMIN)
    public ResponseEntity<ProductDetail> createAdminProduct(ProductCreateRequest req) {
        ProductDetailDto detail = productService.create(
                productCreateRequestToProductCreateRequestDtoMapper.map(req)
        );
        return ResponseEntity.ok(productDetailDtoToProductDetailMapper.map(detail));
    }

    @Override
    @PreAuthorize(AuthorityConstants.ADMIN)
    public ResponseEntity<ProductDetail> updateAdminProduct(Long id, ProductUpdateRequest req) {
        ProductDetailDto detail = productService.update(
                id,
                productUpdateRequestToProductUpdateRequestDtoMapper.map(req),
                SecurityContextUtils.currentUserIdOrThrow()
        );
        return ResponseEntity.ok(productDetailDtoToProductDetailMapper.map(detail));
    }

    @Override
    @PreAuthorize(AuthorityConstants.ADMIN)
    public ResponseEntity<ProductDetail> archiveAdminProduct(Long id, ProductLifecycleRequest req) {
        ProductDetailDto detail = productService.archive(
                id, req.getVersion(), SecurityContextUtils.currentUserIdOrThrow()
        );
        return ResponseEntity.ok(productDetailDtoToProductDetailMapper.map(detail));
    }

    @Override
    @PreAuthorize(AuthorityConstants.ADMIN)
    public ResponseEntity<ProductDetail> restoreAdminProduct(Long id, ProductLifecycleRequest req) {
        ProductDetailDto detail = productService.restore(
                id, req.getVersion(), SecurityContextUtils.currentUserIdOrThrow()
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
