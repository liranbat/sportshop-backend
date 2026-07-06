package com.java.sadna.backend.sportshop.model;

import com.java.sadna.backend.sportshop.model.enums.StockState;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class ProductSizeDto {

    private final String size;
    private final int quantity;
    private final StockState state;
}
