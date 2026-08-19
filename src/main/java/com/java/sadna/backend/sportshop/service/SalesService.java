package com.java.sadna.backend.sportshop.service;

import com.java.sadna.backend.sportshop.common.constants.ErrorConstants;
import com.java.sadna.backend.sportshop.common.constants.SalesConstants;
import com.java.sadna.backend.sportshop.common.util.DatesUtil;
import com.java.sadna.backend.sportshop.common.util.OrderStatusTransitions;
import com.java.sadna.backend.sportshop.config.SalesProperties;
import com.java.sadna.backend.sportshop.entity.ProductEntity;
import com.java.sadna.backend.sportshop.exception.BadRequestException;
import com.java.sadna.backend.sportshop.model.SalesPeriodStatsDto;
import com.java.sadna.backend.sportshop.model.SalesProductRowResult;
import com.java.sadna.backend.sportshop.model.SalesStatusRowResultDto;
import com.java.sadna.backend.sportshop.model.SalesStatusStatsDto;
import com.java.sadna.backend.sportshop.model.SalesSummaryDto;
import com.java.sadna.backend.sportshop.model.SalesTopProductStatsDto;
import com.java.sadna.backend.sportshop.repository.ProductRepository;
import com.java.sadna.backend.sportshop.repository.SalesRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Slf4j
public class SalesService {

    private static final Set<String> CANCELLED_STATUSES = Set.of(
            OrderStatusTransitions.CANCELLED_BY_USER,
            OrderStatusTransitions.CANCELLED_BY_ADMIN);

    private static final List<String> STATUS_BREAKDOWN_ORDER = List.of(
            OrderStatusTransitions.PAID,
            OrderStatusTransitions.SHIPPED,
            OrderStatusTransitions.DELIVERED,
            OrderStatusTransitions.DONE,
            OrderStatusTransitions.CANCELLED_BY_USER,
            OrderStatusTransitions.CANCELLED_BY_ADMIN);

    private static final Set<String> TOP_PRODUCTS_SORT_FIELDS = Set.of(
            SalesConstants.TopProductsSort.REVENUE,
            SalesConstants.TopProductsSort.QUANTITY);

    private final SalesRepository salesRepository;
    private final ProductRepository productRepository;
    private final int topProductsLimit;
    private final Duration maxDateRange;

    public SalesService(SalesRepository salesRepository,
                        ProductRepository productRepository,
                        SalesProperties salesProperties) {
        this.salesRepository = salesRepository;
        this.productRepository = productRepository;
        this.topProductsLimit = salesProperties.getTopProductsLimit();
        this.maxDateRange = salesProperties.getMaxDateRange();
    }

    @Transactional(readOnly = true)
    public SalesSummaryDto getSalesSummary(LocalDate dateFrom, LocalDate dateTo, String topProductsSortBy) {
        log.info("Sales summary started: dateFrom={} dateTo={} topProductsSortBy={}",
                dateFrom, dateTo, topProductsSortBy);

        validateDateRange(dateFrom, dateTo);
        String sortField = resolveTopProductsSortField(topProductsSortBy);

        OffsetDateTime fromInclusive = DatesUtil.utcStartOfDay(dateFrom);
        OffsetDateTime toExclusive = DatesUtil.utcStartOfNextDay(dateTo);
        log.debug("Sales window resolved: fromInclusive={} toExclusive={} sortField={}",
                fromInclusive, toExclusive, sortField);

        Map<String, SalesStatusRowResultDto> byStatus = salesRepository
                .aggregateByStatus(fromInclusive, toExclusive)
                .stream()
                .collect(Collectors.toMap(SalesStatusRowResultDto::getStatus, Function.identity(), (a, b) -> a));
        log.debug("Status aggregate loaded: statusesWithOrders={}", byStatus.keySet());

        List<SalesStatusStatsDto> statusBreakdown = buildStatusBreakdown(byStatus);
        long orderCount = calculateOrderCount(statusBreakdown);
        long cancelledCount = calculateCancelledCount(statusBreakdown);
        BigDecimal revenue = calculateRevenue(statusBreakdown);
        BigDecimal cancelRate = calculateCancelRate(orderCount, cancelledCount);
        BigDecimal averageOrderValue = calculateAverageOrderValue(revenue, orderCount);
        log.debug("KPIs computed: orders={} cancelled={} revenue={} cancelRate={} averageOrderValue={}",
                orderCount, cancelledCount, revenue, cancelRate, averageOrderValue);

        long itemsSoldAmount = calculateItemsSoldAmount(fromInclusive, toExclusive);
        List<SalesTopProductStatsDto> topProducts =
                buildTopProducts(fromInclusive, toExclusive, sortField);

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

    private static List<SalesStatusStatsDto> buildStatusBreakdown(Map<String, SalesStatusRowResultDto> byStatus) {
        List<SalesStatusStatsDto> breakdown = new ArrayList<>(STATUS_BREAKDOWN_ORDER.size());
        for (String status : STATUS_BREAKDOWN_ORDER) {
            SalesStatusRowResultDto row = byStatus.get(status);
            long count = row == null || row.getOrderCount() == null ? 0L : row.getOrderCount();
            breakdown.add(new SalesStatusStatsDto(status, count, statusRevenue(status, row)));
        }
        return breakdown;
    }

    private static BigDecimal statusRevenue(String status, SalesStatusRowResultDto row) {
        if (row == null || row.getRevenue() == null || CANCELLED_STATUSES.contains(status)) {
            return BigDecimal.ZERO;
        }
        return row.getRevenue();
    }

    private static long calculateOrderCount(List<SalesStatusStatsDto> statusBreakdown) {
        return statusBreakdown.stream()
                .filter(row -> !CANCELLED_STATUSES.contains(row.getStatus()))
                .mapToLong(SalesStatusStatsDto::getOrderCount)
                .sum();
    }

    private static long calculateCancelledCount(List<SalesStatusStatsDto> statusBreakdown) {
        return statusBreakdown.stream()
                .filter(row -> CANCELLED_STATUSES.contains(row.getStatus()))
                .mapToLong(SalesStatusStatsDto::getOrderCount)
                .sum();
    }

    private static BigDecimal calculateRevenue(List<SalesStatusStatsDto> statusBreakdown) {
        return statusBreakdown.stream()
                .filter(row -> !CANCELLED_STATUSES.contains(row.getStatus()))
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

    private long calculateItemsSoldAmount(OffsetDateTime fromInclusive, OffsetDateTime toExclusive) {
        Long itemsSold = salesRepository.sumItemsSold(fromInclusive, toExclusive);
        log.debug("Items sold aggregated: itemsSold={}", itemsSold);
        return itemsSold == null ? 0L : itemsSold;
    }

    private List<SalesTopProductStatsDto> buildTopProducts(OffsetDateTime fromInclusive,
                                                           OffsetDateTime toExclusive,
                                                           String sortField) {
        List<? extends SalesProductRowResult> topRows =
                findTopProductRows(fromInclusive, toExclusive, sortField);
        if (topRows.isEmpty()) {
            log.debug("No top products in range — skipping product lookup");
            return List.of();
        }

        Map<Long, ProductEntity> productsById = productRepository
                .findAllById(topRows.stream().map(SalesProductRowResult::getProductId).toList())
                .stream()
                .collect(Collectors.toMap(ProductEntity::getId, Function.identity(), (a, b) -> a));
        log.debug("Top product details loaded: rows={} productsFound={}", topRows.size(), productsById.size());

        return topRows.stream()
                .map(row -> {
                    ProductEntity product = productsById.get(row.getProductId());
                    if (product == null) {
                        log.warn("Top product has no product row (hard-deleted?): productId={}", row.getProductId());
                    }
                    String productName = product != null ? product.getName() : ("#" + row.getProductId());
                    String imageFilename = product != null ? product.getImageFilename() : null;
                    return new SalesTopProductStatsDto(
                            row.getProductId(), productName, imageFilename, row.getResult());
                })
                .toList();
    }

    private List<? extends SalesProductRowResult> findTopProductRows(OffsetDateTime fromInclusive,
                                                                     OffsetDateTime toExclusive,
                                                                     String sortField) {
        Pageable limit = PageRequest.of(0, topProductsLimit);
        log.debug("Top products query: sortField={} limit={}", sortField, topProductsLimit);
        if (SalesConstants.TopProductsSort.QUANTITY.equals(sortField)) {
            return salesRepository.findTopProductsByQuantity(fromInclusive, toExclusive, limit);
        }
        return salesRepository.findTopProductsByRevenue(fromInclusive, toExclusive, limit);
    }
}
