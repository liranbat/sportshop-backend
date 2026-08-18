package com.java.sadna.backend.sportshop.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;

@Getter
@RequiredArgsConstructor
public class SalesTopProductStatsDto {

    private final long productId;
    private final String productName;
    private final String productImageFilename;
    private final BigDecimal result;
}
