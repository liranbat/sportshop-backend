package com.java.sadna.backend.sportshop.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class ProductStockInputDto {

    private final int quantity;
    private final Integer lowStockThreshold;
}
