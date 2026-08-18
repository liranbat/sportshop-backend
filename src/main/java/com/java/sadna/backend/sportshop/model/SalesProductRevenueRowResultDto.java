package com.java.sadna.backend.sportshop.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;

@Getter
@RequiredArgsConstructor
public class SalesProductRevenueRowResultDto implements SalesProductRowResult {

    private final Long productId;
    private final BigDecimal revenue;

    @Override
    public BigDecimal getResult() {
        return revenue;
    }
}
