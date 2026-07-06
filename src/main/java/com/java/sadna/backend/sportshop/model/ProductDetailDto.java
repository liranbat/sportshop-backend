package com.java.sadna.backend.sportshop.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Getter
@RequiredArgsConstructor
public class ProductDetailDto {

    private final ProductDto product;
    private final String categoryName;
    private final List<ProductSizeDto> sizes;
}
