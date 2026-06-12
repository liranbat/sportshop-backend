package com.java.sadna.backend.sportshop.service;

import com.java.sadna.backend.sportshop.entity.OrderEntity;
import com.java.sadna.backend.sportshop.mapper.OrderEntityToOrderSummaryDtoMapper;
import com.java.sadna.backend.sportshop.model.OrderSummaryDto;
import com.java.sadna.backend.sportshop.model.PagedResult;
import com.java.sadna.backend.sportshop.repository.OrderRepository;
import com.java.sadna.backend.sportshop.repository.specification.OrderSpecifications;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

@Service
public class OrderService {

    private static final String SORT_FIELD_TOTAL = "total";
    private static final String SORT_PATH_CREATED_AT = "createdAt";
    private static final String SORT_PATH_TOTAL_PRICE = "totalPrice";
    private static final String SORT_PATH_ID = "id";
    private static final String SORT_DIRECTION_ASC = "asc";

    private static final int DEFAULT_PAGE_SIZE = 10;

    private final OrderRepository orderRepository;
    private final OrderEntityToOrderSummaryDtoMapper orderEntityToOrderSummaryDtoMapper;
    private final PaginationService paginationService;

    public OrderService(OrderRepository orderRepository,
                        OrderEntityToOrderSummaryDtoMapper orderEntityToOrderSummaryDtoMapper,
                        PaginationService paginationService) {
        this.orderRepository = orderRepository;
        this.orderEntityToOrderSummaryDtoMapper = orderEntityToOrderSummaryDtoMapper;
        this.paginationService = paginationService;
    }

    @Transactional(readOnly = true)
    public PagedResult<OrderSummaryDto> listForUser(Long userId,
                                                    String status,
                                                    String orderNumberSearch,
                                                    BigDecimal amountMin,
                                                    BigDecimal amountMax,
                                                    LocalDate dateFrom,
                                                    LocalDate dateTo,
                                                    String sortField,
                                                    String sortDirection,
                                                    Integer page,
                                                    Integer pageSize) {
        Specification<OrderEntity> spec = Specification.allOf(
                OrderSpecifications.userIdEquals(userId),
                OrderSpecifications.statusEquals(status),
                OrderSpecifications.orderNumberContainsIgnoreCase(orderNumberSearch),
                OrderSpecifications.totalPriceGte(amountMin),
                OrderSpecifications.totalPriceLte(amountMax),
                OrderSpecifications.createdAtGte(toUtcStartOfDay(dateFrom)),
                OrderSpecifications.createdAtLt(toUtcStartOfDayExclusive(dateTo))
        );

        Sort sort = buildSort(sortField, sortDirection);

        return paginationService.paginate(
                orderRepository, spec, sort, page, pageSize, DEFAULT_PAGE_SIZE,
                orderEntityToOrderSummaryDtoMapper
        );
    }

    // Lower bound for dateFrom: 00:00:00Z of the same day, used with `>=`.
    private OffsetDateTime toUtcStartOfDay(LocalDate date) {
        return date == null ? null : date.atStartOfDay().atOffset(ZoneOffset.UTC);
    }

    // Upper bound for dateTo: 00:00:00Z of the NEXT day, used with `<`, so the entire
    // dateTo day is included without needing 23:59:59.999... gymnastics.
    private OffsetDateTime toUtcStartOfDayExclusive(LocalDate date) {
        return date == null ? null : date.plusDays(1).atStartOfDay().atOffset(ZoneOffset.UTC);
    }

    private Sort buildSort(String sortField, String sortDirection) {
        if (sortField == null || sortField.isBlank()) {
            return Sort.by(Sort.Order.desc(SORT_PATH_CREATED_AT), Sort.Order.asc(SORT_PATH_ID));
        }
        Sort.Direction direction = SORT_DIRECTION_ASC.equalsIgnoreCase(sortDirection)
                ? Sort.Direction.ASC
                : Sort.Direction.DESC;
        String primary = SORT_FIELD_TOTAL.equalsIgnoreCase(sortField)
                ? SORT_PATH_TOTAL_PRICE
                : SORT_PATH_CREATED_AT;
        return Sort.by(new Sort.Order(direction, primary), Sort.Order.asc(SORT_PATH_ID));
    }
}
