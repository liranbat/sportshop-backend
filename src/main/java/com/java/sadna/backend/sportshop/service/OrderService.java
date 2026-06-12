package com.java.sadna.backend.sportshop.service;

import com.java.sadna.backend.sportshop.entity.OrderEntity;
import com.java.sadna.backend.sportshop.entity.PaymentEntity;
import com.java.sadna.backend.sportshop.exception.NotFoundException;
import com.java.sadna.backend.sportshop.mapper.OrderEntityToOrderSummaryDtoMapper;
import com.java.sadna.backend.sportshop.mapper.OrderItemEntityToOrderItemDtoMapper;
import com.java.sadna.backend.sportshop.mapper.PaymentEntityToOrderPaymentDtoMapper;
import com.java.sadna.backend.sportshop.model.OrderDetailDto;
import com.java.sadna.backend.sportshop.model.OrderItemDto;
import com.java.sadna.backend.sportshop.model.OrderPaymentDto;
import com.java.sadna.backend.sportshop.model.OrderSummaryDto;
import com.java.sadna.backend.sportshop.model.PagedResult;
import com.java.sadna.backend.sportshop.model.ShippingDetailsDto;
import com.java.sadna.backend.sportshop.repository.OrderItemRepository;
import com.java.sadna.backend.sportshop.repository.OrderRepository;
import com.java.sadna.backend.sportshop.repository.PaymentRepository;
import com.java.sadna.backend.sportshop.repository.specification.OrderSpecifications;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;

@Service
public class OrderService {

    private static final String SORT_FIELD_TOTAL = "total";
    private static final String SORT_PATH_CREATED_AT = "createdAt";
    private static final String SORT_PATH_TOTAL_PRICE = "totalPrice";
    private static final String SORT_PATH_ID = "id";
    private static final String SORT_DIRECTION_ASC = "asc";

    private static final int DEFAULT_PAGE_SIZE = 10;

    private static final String MSG_ORDER_NOT_FOUND = "Order not found.";

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final PaymentRepository paymentRepository;
    private final OrderEntityToOrderSummaryDtoMapper orderEntityToOrderSummaryDtoMapper;
    private final OrderItemEntityToOrderItemDtoMapper orderItemEntityToOrderItemDtoMapper;
    private final PaymentEntityToOrderPaymentDtoMapper paymentEntityToOrderPaymentDtoMapper;
    private final PaginationService paginationService;

    public OrderService(OrderRepository orderRepository,
                        OrderItemRepository orderItemRepository,
                        PaymentRepository paymentRepository,
                        OrderEntityToOrderSummaryDtoMapper orderEntityToOrderSummaryDtoMapper,
                        OrderItemEntityToOrderItemDtoMapper orderItemEntityToOrderItemDtoMapper,
                        PaymentEntityToOrderPaymentDtoMapper paymentEntityToOrderPaymentDtoMapper,
                        PaginationService paginationService) {
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
        this.paymentRepository = paymentRepository;
        this.orderEntityToOrderSummaryDtoMapper = orderEntityToOrderSummaryDtoMapper;
        this.orderItemEntityToOrderItemDtoMapper = orderItemEntityToOrderItemDtoMapper;
        this.paymentEntityToOrderPaymentDtoMapper = paymentEntityToOrderPaymentDtoMapper;
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

    @Transactional(readOnly = true)
    public OrderDetailDto getDetailForUser(String orderNumber, Long userId) {
        // Owner gate: missing OR not-owned both surface as the same 404, no information leak.
        OrderEntity order = orderRepository.findByOrderNumberAndUserId(orderNumber, userId)
                .orElseThrow(() -> new NotFoundException(MSG_ORDER_NOT_FOUND));

        List<OrderItemDto> items = orderItemRepository.findByOrderIdOrderByIdAsc(order.getId()).stream()
                .map(orderItemEntityToOrderItemDtoMapper::map)
                .toList();

        PaymentEntity payment = paymentRepository.findByOrderId(order.getId())
                .orElseThrow(() -> new IllegalStateException(
                        "Payment row missing for orderNumber=" + order.getOrderNumber()));
        OrderPaymentDto paymentDto = paymentEntityToOrderPaymentDtoMapper.map(payment);

        ShippingDetailsDto shipping = new ShippingDetailsDto(
                order.getShippingFullName(),
                order.getShippingEmail(),
                order.getShippingPhone(),
                order.getShippingCountry(),
                order.getShippingCity(),
                order.getShippingAddressLine()
        );

        return new OrderDetailDto(
                order.getOrderNumber(),
                order.getStatus(),
                order.getCreatedAt(),
                order.getCancelledAt(),
                order.getTotalPrice(),
                order.getItemCount(),
                items,
                shipping,
                paymentDto
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
