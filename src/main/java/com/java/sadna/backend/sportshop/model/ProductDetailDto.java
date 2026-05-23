package com.java.sadna.backend.sportshop.model;

import java.util.List;

public class ProductDetailDto implements BaseDto {

    private final ProductDto product;
    private final String categoryName;
    private final List<ProductSizeDto> sizes;

    public ProductDetailDto(ProductDto product, String categoryName, List<ProductSizeDto> sizes) {
        this.product = product;
        this.categoryName = categoryName;
        this.sizes = sizes;
    }

    public ProductDto getProduct() {
        return product;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public List<ProductSizeDto> getSizes() {
        return sizes;
    }
}
