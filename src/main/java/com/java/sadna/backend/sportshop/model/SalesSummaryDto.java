package com.java.sadna.backend.sportshop.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Getter
@RequiredArgsConstructor
public class SalesSummaryDto {

    private final LocalDate dateFrom;
    private final LocalDate dateTo;
    private final String topProductsSortBy;
    private final SalesPeriodStatsDto summary;
    private final List<SalesStatusStatsDto> statusBreakdown;
    private final List<SalesTopProductStatsDto> topProducts;
}
