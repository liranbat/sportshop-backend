package com.java.sadna.backend.sportshop.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;

@Getter
@RequiredArgsConstructor
public class SalesStatusStatsDto {

    private final String status;
    private final long orderCount;
    private final BigDecimal revenue;
}
