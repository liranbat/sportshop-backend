package com.java.sadna.backend.sportshop.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;

@Getter
@RequiredArgsConstructor
public class SalesPeriodStatsDto {

    private final long orderCount;
    private final BigDecimal revenue;
    private final long cancelledCount;
    private final BigDecimal cancelRate;
    private final BigDecimal averageOrderValue;
    private final long itemsSoldAmount;
}
