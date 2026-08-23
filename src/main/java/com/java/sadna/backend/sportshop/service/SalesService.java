package com.java.sadna.backend.sportshop.service;

import com.java.sadna.backend.sportshop.common.constants.AsyncConstants;
import com.java.sadna.backend.sportshop.common.constants.ErrorConstants;
import com.java.sadna.backend.sportshop.common.constants.SalesConstants;
import com.java.sadna.backend.sportshop.common.util.DatesUtil;
import com.java.sadna.backend.sportshop.common.util.FuturesUtil;
import com.java.sadna.backend.sportshop.config.SalesProperties;
import com.java.sadna.backend.sportshop.config.async.SalesExecutorProperties;
import com.java.sadna.backend.sportshop.exception.BadRequestException;
import com.java.sadna.backend.sportshop.model.SalesPeriodStatsDto;
import com.java.sadna.backend.sportshop.model.SalesStatusStatsDto;
import com.java.sadna.backend.sportshop.model.SalesSummaryDto;
import com.java.sadna.backend.sportshop.model.SalesTopProductStatsDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

@Service
@Slf4j
public class SalesService {

    private static final Set<String> TOP_PRODUCTS_SORT_FIELDS = Set.of(
            SalesConstants.TopProductsSort.REVENUE,
            SalesConstants.TopProductsSort.QUANTITY);

    private final SalesAggregationService salesAggregationService;
    private final Executor salesFanoutExecutor;
    private final Duration maxDateRange;
    private final Duration resultsTimeout;

    public SalesService(SalesAggregationService salesAggregationService,
                        @Qualifier(AsyncConstants.Executors.SALES_FANOUT) Executor salesFanoutExecutor,
                        SalesProperties salesProperties,
                        SalesExecutorProperties salesExecutorProperties) {
        this.salesAggregationService = salesAggregationService;
        this.salesFanoutExecutor = salesFanoutExecutor;
        this.maxDateRange = salesProperties.getMaxDateRange();
        this.resultsTimeout = salesExecutorProperties.getResultsTimeout();
    }

    public SalesSummaryDto getSalesSummary(LocalDate dateFrom, LocalDate dateTo, String topProductsSortBy) {
        log.info("Sales summary started: dateFrom={} dateTo={} topProductsSortBy={}",
                dateFrom, dateTo, topProductsSortBy);

        validateDateRange(dateFrom, dateTo);
        String sortField = resolveTopProductsSortField(topProductsSortBy);

        OffsetDateTime fromInclusive = DatesUtil.utcStartOfDay(dateFrom);
        OffsetDateTime toExclusive = DatesUtil.utcStartOfNextDay(dateTo);
        log.debug("Sales window resolved: fromInclusive={} toExclusive={} sortField={}",
                fromInclusive, toExclusive, sortField);

        CompletableFuture<List<SalesStatusStatsDto>> statusFuture = CompletableFuture.supplyAsync(
                () -> salesAggregationService.loadStatusBreakdown(fromInclusive, toExclusive),
                salesFanoutExecutor);
        CompletableFuture<Long> itemsSoldFuture = CompletableFuture.supplyAsync(
                () -> salesAggregationService.loadItemsSoldAmount(fromInclusive, toExclusive),
                salesFanoutExecutor);
        CompletableFuture<List<SalesTopProductStatsDto>> topProductsFuture = CompletableFuture.supplyAsync(
                () -> salesAggregationService.loadTopProducts(fromInclusive, toExclusive, sortField),
                salesFanoutExecutor);

        List<SalesStatusStatsDto> statusBreakdown = FuturesUtil.join(resultsTimeout, statusFuture);
        long itemsSoldAmount = FuturesUtil.join(resultsTimeout, itemsSoldFuture);
        List<SalesTopProductStatsDto> topProducts = FuturesUtil.join(resultsTimeout, topProductsFuture);

        long orderCount = calculateOrderCount(statusBreakdown);
        long cancelledCount = calculateCancelledCount(statusBreakdown);
        BigDecimal revenue = calculateRevenue(statusBreakdown);
        BigDecimal cancelRate = calculateCancelRate(orderCount, cancelledCount);
        BigDecimal averageOrderValue = calculateAverageOrderValue(revenue, orderCount);
        log.debug("KPIs computed: orders={} cancelled={} revenue={} cancelRate={} averageOrderValue={}",
                orderCount, cancelledCount, revenue, cancelRate, averageOrderValue);

        SalesPeriodStatsDto summary = new SalesPeriodStatsDto(
                orderCount,
                revenue,
                cancelledCount,
                cancelRate,
                averageOrderValue,
                itemsSoldAmount
        );

        log.info("Sales summary completed: dateFrom={} dateTo={} sortField={} orders={} revenue={} cancelled={} itemsSold={} topProducts={}",
                dateFrom, dateTo, sortField, orderCount, revenue, cancelledCount, itemsSoldAmount, topProducts.size());

        return new SalesSummaryDto(dateFrom, dateTo, sortField, summary, statusBreakdown, topProducts);
    }

    private static String resolveTopProductsSortField(String sortField) {
        if (sortField == null || sortField.isBlank()) {
            return SalesConstants.TopProductsSort.REVENUE;
        }
        String normalized = sortField.trim().toLowerCase();
        if (!TOP_PRODUCTS_SORT_FIELDS.contains(normalized)) {
            log.warn("Sales summary rejected (unknown sort field): topProductsSortBy={} allowed={}",
                    sortField, TOP_PRODUCTS_SORT_FIELDS);
            throw new BadRequestException(ErrorConstants.Http.BAD_REQUEST_UNKNOWN_SORT_FIELD, sortField);
        }
        return normalized;
    }

    private void validateDateRange(LocalDate dateFrom, LocalDate dateTo) {
        if (dateFrom == null || dateTo == null || dateFrom.isAfter(dateTo)) {
            log.warn("Sales summary rejected (invalid range): dateFrom={} dateTo={}", dateFrom, dateTo);
            throw new BadRequestException(ErrorConstants.Sales.DATE_RANGE_INVALID);
        }
        if (dateFrom.plusDays(maxDateRange.toDays()).isBefore(dateTo)) {
            log.warn("Sales summary rejected (range too long): dateFrom={} dateTo={} maxDays={}",
                    dateFrom, dateTo, maxDateRange.toDays());
            throw new BadRequestException(ErrorConstants.Sales.DATE_RANGE_TOO_LONG, maxDateRange.toDays());
        }
    }

    private static long calculateOrderCount(List<SalesStatusStatsDto> statusBreakdown) {
        return statusBreakdown.stream()
                .filter(row -> !SalesConstants.CANCELLED_STATUSES.contains(row.getStatus()))
                .mapToLong(SalesStatusStatsDto::getOrderCount)
                .sum();
    }

    private static long calculateCancelledCount(List<SalesStatusStatsDto> statusBreakdown) {
        return statusBreakdown.stream()
                .filter(row -> SalesConstants.CANCELLED_STATUSES.contains(row.getStatus()))
                .mapToLong(SalesStatusStatsDto::getOrderCount)
                .sum();
    }

    private static BigDecimal calculateRevenue(List<SalesStatusStatsDto> statusBreakdown) {
        return statusBreakdown.stream()
                .filter(row -> !SalesConstants.CANCELLED_STATUSES.contains(row.getStatus()))
                .map(SalesStatusStatsDto::getRevenue)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private static BigDecimal calculateCancelRate(long orderCount, long cancelledCount) {
        long totalOrders = orderCount + cancelledCount;
        if (totalOrders == 0) {
            return BigDecimal.ZERO;
        }
        // scale + rounding are mandatory here, divide() throws on a non-terminating result
        return BigDecimal.valueOf(cancelledCount * 100L)
                .divide(BigDecimal.valueOf(totalOrders), 1, RoundingMode.HALF_UP);
    }

    private static BigDecimal calculateAverageOrderValue(BigDecimal revenue, long orderCount) {
        if (orderCount == 0) {
            return BigDecimal.ZERO;
        }
        return revenue.divide(BigDecimal.valueOf(orderCount), 2, RoundingMode.HALF_UP);
    }
}
