package com.java.sadna.backend.sportshop.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;

@Getter
@RequiredArgsConstructor
public class SalesProductQuantityRowResultDto implements SalesProductRowResult {

    private final Long productId;
    private final Long quantitySold;

    @Override
    public BigDecimal getResult() {
        return BigDecimal.valueOf(quantitySold);
    }
}
