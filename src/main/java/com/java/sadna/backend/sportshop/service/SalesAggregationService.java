package com.java.sadna.backend.sportshop.service;

import com.java.sadna.backend.sportshop.common.constants.SalesConstants;
import com.java.sadna.backend.sportshop.common.util.OrderStatusTransitions;
import com.java.sadna.backend.sportshop.config.SalesProperties;
import com.java.sadna.backend.sportshop.entity.ProductEntity;
import com.java.sadna.backend.sportshop.model.SalesProductRowResult;
import com.java.sadna.backend.sportshop.model.SalesStatusRowResultDto;
import com.java.sadna.backend.sportshop.model.SalesStatusStatsDto;
import com.java.sadna.backend.sportshop.model.SalesTopProductStatsDto;
import com.java.sadna.backend.sportshop.repository.ProductRepository;
import com.java.sadna.backend.sportshop.repository.SalesRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Slf4j
public class SalesAggregationService {

    private static final List<String> STATUS_BREAKDOWN_ORDER = List.of(
            OrderStatusTransitions.PAID,
            OrderStatusTransitions.SHIPPED,
            OrderStatusTransitions.DELIVERED,
            OrderStatusTransitions.DONE,
            OrderStatusTransitions.CANCELLED_BY_USER,
            OrderStatusTransitions.CANCELLED_BY_ADMIN);

    private final SalesRepository salesRepository;
    private final ProductRepository productRepository;
    private final int topProductsLimit;

    public SalesAggregationService(SalesRepository salesRepository,
                                   ProductRepository productRepository,
                                   SalesProperties salesProperties) {
        this.salesRepository = salesRepository;
        this.productRepository = productRepository;
        this.topProductsLimit = salesProperties.getTopProductsLimit();
    }

    @Transactional(readOnly = true)
    public List<SalesStatusStatsDto> loadStatusBreakdown(OffsetDateTime fromInclusive,
                                                         OffsetDateTime toExclusive) {
        Map<String, SalesStatusRowResultDto> byStatus = salesRepository
                .aggregateByStatus(fromInclusive, toExclusive)
                .stream()
                .collect(Collectors.toMap(SalesStatusRowResultDto::getStatus, Function.identity(), (a, b) -> a));
        log.debug("Status aggregate loaded: statusesWithOrders={}", byStatus.keySet());

        List<SalesStatusStatsDto> breakdown = new ArrayList<>(STATUS_BREAKDOWN_ORDER.size());
        for (String status : STATUS_BREAKDOWN_ORDER) {
            SalesStatusRowResultDto row = byStatus.get(status);
            long count = row == null || row.getOrderCount() == null ? 0L : row.getOrderCount();
            breakdown.add(new SalesStatusStatsDto(status, count, statusRevenue(status, row)));
        }
        return breakdown;
    }

    @Transactional(readOnly = true)
    public long loadItemsSoldAmount(OffsetDateTime fromInclusive, OffsetDateTime toExclusive) {
        Long itemsSold = salesRepository.sumItemsSold(fromInclusive, toExclusive);
        log.debug("Items sold aggregated: itemsSold={}", itemsSold);
        return itemsSold == null ? 0L : itemsSold;
    }

    @Transactional(readOnly = true)
    public List<SalesTopProductStatsDto> loadTopProducts(OffsetDateTime fromInclusive,
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

    private static BigDecimal statusRevenue(String status, SalesStatusRowResultDto row) {
        if (row == null || row.getRevenue() == null || SalesConstants.CANCELLED_STATUSES.contains(status)) {
            return BigDecimal.ZERO;
        }
        return row.getRevenue();
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
