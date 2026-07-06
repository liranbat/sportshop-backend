package com.java.sadna.backend.sportshop.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;
import java.util.Map;

@Getter
@RequiredArgsConstructor
public class ProductCreateRequestDto {

    private final String name;
    private final String description;
    private final Long categoryId;
    private final boolean multiSize;
    private final String imageUrl;
    private final BigDecimal price;
    private final Map<String, ProductStockInputDto> stockBySize;
}
