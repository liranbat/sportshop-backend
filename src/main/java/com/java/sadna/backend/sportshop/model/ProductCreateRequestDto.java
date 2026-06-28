package com.java.sadna.backend.sportshop.model;

import java.math.BigDecimal;
import java.util.Map;

public class ProductCreateRequestDto implements BaseDto {

    private final String name;
    private final String description;
    private final Long categoryId;
    private final boolean multiSize;
    private final String imageUrl;
    private final BigDecimal price;
    private final Map<String, ProductStockInputDto> stockBySize;

    public ProductCreateRequestDto(String name,
                                   String description,
                                   Long categoryId,
                                   boolean multiSize,
                                   String imageUrl,
                                   BigDecimal price,
                                   Map<String, ProductStockInputDto> stockBySize) {
        this.name = name;
        this.description = description;
        this.categoryId = categoryId;
        this.multiSize = multiSize;
        this.imageUrl = imageUrl;
        this.price = price;
        this.stockBySize = stockBySize;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public boolean isMultiSize() {
        return multiSize;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public Map<String, ProductStockInputDto> getStockBySize() {
        return stockBySize;
    }
}
