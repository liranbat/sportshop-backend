package com.java.sadna.backend.sportshop.controller;

import com.java.sadna.backend.sportshop.api.generated.products.api.ProductsApi;
import com.java.sadna.backend.sportshop.api.generated.products.model.Product;
import com.java.sadna.backend.sportshop.mapper.ProductDtoToProductMapper;
import com.java.sadna.backend.sportshop.service.ProductService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.List;

@RestController
public class ProductController implements ProductsApi {

    private final ProductService productService;
    private final ProductDtoToProductMapper productDtoToProductMapper;

    public ProductController(ProductService productService,
                             ProductDtoToProductMapper productDtoToProductMapper) {
        this.productService = productService;
        this.productDtoToProductMapper = productDtoToProductMapper;
    }

    @Override
    public ResponseEntity<List<Product>> listProducts(Boolean active,
                                                      String search,
                                                      List<Long> categoryIds,
                                                      BigDecimal priceMin,
                                                      BigDecimal priceMax,
                                                      String sortField,
                                                      String sortDirection) {
        List<Product> body = productService
                .list(active, search, categoryIds, priceMin, priceMax, sortField, sortDirection)
                .stream()
                .map(productDtoToProductMapper::map)
                .toList();
        return ResponseEntity.ok(body);
    }
}
