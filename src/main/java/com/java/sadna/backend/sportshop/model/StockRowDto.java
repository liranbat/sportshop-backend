package com.java.sadna.backend.sportshop.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class StockRowDto {

    private final Long productId;
    private final String productName;
    private final String productImageFilename;
    private final boolean productIsArchived;
    private final boolean productIsMultiSize;
    private final String size;
    private final int quantity;
    private final Integer lowStockThreshold;
}
