package com.java.sadna.backend.sportshop.service;

import com.java.sadna.backend.sportshop.entity.OrderEntity;
import com.java.sadna.backend.sportshop.entity.OrderItemEntity;
import com.java.sadna.backend.sportshop.entity.PaymentEntity;
import com.java.sadna.backend.sportshop.exception.ConflictException;
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
import com.java.sadna.backend.sportshop.repository.ProductStockRepository;
import com.java.sadna.backend.sportshop.repository.specification.OrderSpecifications;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Comparator;
import java.util.List;

@Service
public class OrderService {

    private static final Logger log = LoggerFactory.getLogger(OrderService.class);

    private static final String SORT_FIELD_TOTAL = "total";
    private static final String SORT_PATH_CREATED_AT = "createdAt";
    private static final String SORT_PATH_TOTAL_PRICE = "totalPrice";
    private static final String SORT_PATH_ID = "id";
    private static final String SORT_DIRECTION_ASC = "asc";

    private static final int DEFAULT_PAGE_SIZE = 10;

    private static final String STATUS_PAID = "PAID";

    private static final String MSG_ORDER_NOT_FOUND = "Order not found.";
    private static final String MSG_ORDER_CANNOT_BE_CANCELLED = "This order can no longer be cancelled.";
    private static final String MSG_PAYMENT_REFUND_INVARIANT =
            "Payment row not in SUCCESS state for cancelled order; transaction rolled back.";

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final PaymentRepository paymentRepository;
    private final ProductStockRepository productStockRepository;
    private final OrderEntityToOrderSummaryDtoMapper orderEntityToOrderSummaryDtoMapper;
    private final OrderItemEntityToOrderItemDtoMapper orderItemEntityToOrderItemDtoMapper;
    private final PaymentEntityToOrderPaymentDtoMapper paymentEntityToOrderPaymentDtoMapper;
    private final PaginationService paginationService;

    public OrderService(OrderRepository orderRepository,
                        OrderItemRepository orderItemRepository,
                        PaymentRepository paymentRepository,
                        ProductStockRepository productStockRepository,
                        OrderEntityToOrderSummaryDtoMapper orderEntityToOrderSummaryDtoMapper,
                        OrderItemEntityToOrderItemDtoMapper orderItemEntityToOrderItemDtoMapper,
                        PaymentEntityToOrderPaymentDtoMapper paymentEntityToOrderPaymentDtoMapper,
                        PaginationService paginationService) {
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
        this.paymentRepository = paymentRepository;
        this.productStockRepository = productStockRepository;
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

    @Transactional
    public void cancelForUser(String orderNumber, Long userId) {
        log.info("Cancel started: userId={} orderNumber={}", userId, orderNumber);

        OrderEntity order = orderRepository.findByOrderNumberAndUserId(orderNumber, userId)
                .orElseThrow(() -> new NotFoundException(MSG_ORDER_NOT_FOUND));

        if (!STATUS_PAID.equals(order.getStatus())) {
            log.warn("Cancel rejected: orderId={} userId={} currentStatus={}",
                    order.getId(), userId, order.getStatus());
            throw new ConflictException(MSG_ORDER_CANNOT_BE_CANCELLED);
        }

        int orderAffected = orderRepository.cancelOwnUserOrder(order.getId(), userId);
        if (orderAffected == 0) {
            log.warn("Cancel race: orderId={} userId={} -- order moved off PAID between pre-flight and write",
                    order.getId(), userId);
            throw new ConflictException(MSG_ORDER_CANNOT_BE_CANCELLED);
        }

        // sort by (productId, size) so parallel cancels of *different* orders that share
        // product_stock rows hit them in the same order as checkouts -- no deadlocks
        List<OrderItemEntity> items = orderItemRepository.findByOrderIdOrderByIdAsc(order.getId()).stream()
                .sorted(Comparator
                        .comparing(OrderItemEntity::getProductId)
                        .thenComparing(OrderItemEntity::getSize))
                .toList();
        for (OrderItemEntity item : items) {
            int restored = productStockRepository.restore(
                    item.getProductId(), item.getSize(), item.getQuantity());
            if (restored == 0) {
                log.warn("Stock restore skipped (product_stock row missing): orderId={} productId={} size={} qty={}",
                        order.getId(), item.getProductId(), item.getSize(), item.getQuantity());
            }
        }

        int refunded = paymentRepository.refundIfSuccess(order.getId(), userId);
        if (refunded == 0) {
            log.error("Payment refund invariant break: orderId={} userId={}", order.getId(), userId);
            throw new IllegalStateException(MSG_PAYMENT_REFUND_INVARIANT);
        }

        log.info("Cancel completed: orderId={} userId={} orderNumber={}",
                order.getId(), userId, orderNumber);
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
