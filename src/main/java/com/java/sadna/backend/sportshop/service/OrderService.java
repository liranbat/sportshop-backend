package com.java.sadna.backend.sportshop.service;

import com.java.sadna.backend.sportshop.entity.OrderEntity;
import com.java.sadna.backend.sportshop.entity.OrderItemEntity;
import com.java.sadna.backend.sportshop.entity.PaymentEntity;
import com.java.sadna.backend.sportshop.exception.BadRequestException;
import com.java.sadna.backend.sportshop.exception.ConflictException;
import com.java.sadna.backend.sportshop.exception.NotFoundException;
import com.java.sadna.backend.sportshop.mapper.OrderEntityToOrderSummaryDtoMapper;
import com.java.sadna.backend.sportshop.mapper.OrderItemEntityToOrderItemDtoMapper;
import com.java.sadna.backend.sportshop.mapper.PaymentEntityToOrderPaymentDtoMapper;
import com.java.sadna.backend.sportshop.mapper.UserEntityToCustomerForOrderDtoMapper;
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
import com.java.sadna.backend.sportshop.util.OrderStatusTransitions;
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
import java.util.Set;

@Service
public class OrderService {

    private static final Logger log = LoggerFactory.getLogger(OrderService.class);

    private static final String SORT_FIELD_TOTAL = "total";
    private static final String SORT_PATH_CREATED_AT = "createdAt";
    private static final String SORT_PATH_TOTAL_PRICE = "totalPrice";
    private static final String SORT_PATH_ID = "id";
    private static final String SORT_DIRECTION_ASC = "asc";

    private static final int DEFAULT_PAGE_SIZE = 10;

    private static final Set<String> ADMIN_CANCELLABLE_STATUSES = Set.of(
            OrderStatusTransitions.STATUS_PAID,
            OrderStatusTransitions.STATUS_SHIPPED,
            OrderStatusTransitions.STATUS_DELIVERED);

    private static final List<String> ADMIN_EDITABLE_SHIPPING_STATUSES = List.of(
            OrderStatusTransitions.STATUS_PAID,
            OrderStatusTransitions.STATUS_SHIPPED,
            OrderStatusTransitions.STATUS_DELIVERED);

    private static final String MSG_ORDER_NOT_FOUND = "Order not found.";
    private static final String MSG_ORDER_CANNOT_BE_CANCELLED = "This order can no longer be cancelled.";
    private static final String MSG_INVALID_STATUS_TRANSITION = "Invalid status transition: %s -> %s.";
    private static final String MSG_ORDER_STATUS_CHANGED = "Order status changed since you opened the form.";
    private static final String MSG_SHIPPING_NOT_EDITABLE =
            "Shipping address can only be edited while the order is in "
                    + String.join(", ", ADMIN_EDITABLE_SHIPPING_STATUSES) + ".";
    private static final String MSG_PAYMENT_REFUND_INVARIANT =
            "Payment row not in SUCCESS state for cancelled order; transaction rolled back.";

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final PaymentRepository paymentRepository;
    private final ProductStockRepository productStockRepository;
    private final OrderEntityToOrderSummaryDtoMapper orderEntityToOrderSummaryDtoMapper;
    private final OrderItemEntityToOrderItemDtoMapper orderItemEntityToOrderItemDtoMapper;
    private final PaymentEntityToOrderPaymentDtoMapper paymentEntityToOrderPaymentDtoMapper;
    private final UserEntityToCustomerForOrderDtoMapper userEntityToCustomerForOrderDtoMapper;
    private final PaginationService paginationService;

    public OrderService(OrderRepository orderRepository,
                        OrderItemRepository orderItemRepository,
                        PaymentRepository paymentRepository,
                        ProductStockRepository productStockRepository,
                        OrderEntityToOrderSummaryDtoMapper orderEntityToOrderSummaryDtoMapper,
                        OrderItemEntityToOrderItemDtoMapper orderItemEntityToOrderItemDtoMapper,
                        PaymentEntityToOrderPaymentDtoMapper paymentEntityToOrderPaymentDtoMapper,
                        UserEntityToCustomerForOrderDtoMapper userEntityToCustomerForOrderDtoMapper,
                        PaginationService paginationService) {
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
        this.paymentRepository = paymentRepository;
        this.productStockRepository = productStockRepository;
        this.orderEntityToOrderSummaryDtoMapper = orderEntityToOrderSummaryDtoMapper;
        this.orderItemEntityToOrderItemDtoMapper = orderItemEntityToOrderItemDtoMapper;
        this.paymentEntityToOrderPaymentDtoMapper = paymentEntityToOrderPaymentDtoMapper;
        this.userEntityToCustomerForOrderDtoMapper = userEntityToCustomerForOrderDtoMapper;
        this.paginationService = paginationService;
    }

    @Transactional(readOnly = true)
    public PagedResult<OrderSummaryDto> list(Long userId,
                                             String status,
                                             String orderNumberSearch,
                                             String customer,
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
                OrderSpecifications.customerMatches(customer),
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

    // userId == null -> admin context, no owner gate; 404 only when the order number doesn't exist.
    // userId != null -> regular user; the finder's WHERE user_id = :userId clause proves the order
    //   belongs to the caller. A row owned by someone else won't match and surfaces as the same
    //   404 as a missing order, so we never leak whether the order exists for a different user.
    @Transactional(readOnly = true)
    public OrderDetailDto getDetail(String orderNumber, Long userId) {
        OrderEntity order = (userId == null
                ? orderRepository.findWithUserByOrderNumber(orderNumber)
                : orderRepository.findWithUserByOrderNumberAndUserId(orderNumber, userId))
                .orElseThrow(() -> new NotFoundException(MSG_ORDER_NOT_FOUND));
        return buildDetailDto(order);
    }

    private OrderDetailDto buildDetailDto(OrderEntity order) {
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
                paymentDto,
                userEntityToCustomerForOrderDtoMapper.map(order.getUser())
        );
    }

    // userId == null -> admin path (no owner gate, broader status set); userId != null -> user
    // path (owner gate, PAID-only). actorId is the acting caller (== userId for users) and
    // lands on cancelled_by + updated_by.
    @Transactional
    public void cancel(String orderNumber, Long userId, Long actorId) {
        boolean isAdmin = (userId == null);
        log.info("Cancel started: actorId={} isAdmin={} orderNumber={}", actorId, isAdmin, orderNumber);

        OrderEntity order = (isAdmin
                ? orderRepository.findWithUserByOrderNumber(orderNumber)
                : orderRepository.findByOrderNumberAndUserId(orderNumber, userId))
                .orElseThrow(() -> new NotFoundException(MSG_ORDER_NOT_FOUND));

        boolean cancellable = isAdmin
                ? ADMIN_CANCELLABLE_STATUSES.contains(order.getStatus())
                : OrderStatusTransitions.STATUS_PAID.equals(order.getStatus());
        if (!cancellable) {
            log.warn("Cancel rejected: orderId={} actorId={} isAdmin={} currentStatus={}",
                    order.getId(), actorId, isAdmin, order.getStatus());
            throw new ConflictException(MSG_ORDER_CANNOT_BE_CANCELLED);
        }

        int orderAffected = orderRepository.cancel(order.getId(), isAdmin, actorId);
        if (orderAffected == 0) {
            log.warn("Cancel race: orderId={} actorId={} isAdmin={} -- order moved out of the cancellable set between pre-flight and write",
                    order.getId(), actorId, isAdmin);
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

        int refunded = paymentRepository.refundIfSuccess(order.getId(), actorId);
        if (refunded == 0) {
            log.error("Payment refund invariant break: orderId={} actorId={}", order.getId(), actorId);
            throw new IllegalStateException(MSG_PAYMENT_REFUND_INVARIANT);
        }

        log.info("Cancel completed: orderId={} actorId={} isAdmin={} orderNumber={}",
                order.getId(), actorId, isAdmin, orderNumber);
    }

    // Admin-only. priorStatus is the OCC anchor (pre-check + SQL WHERE).
    @Transactional
    public void updateStatus(String orderNumber, String priorStatus, String targetStatus, Long adminId) {
        log.info("Update status started: adminId={} orderNumber={} prior={} target={}",
                adminId, orderNumber, priorStatus, targetStatus);

        if (!OrderStatusTransitions.isAllowed(priorStatus, targetStatus)) {
            log.warn("Update status rejected (illegal transition): adminId={} orderNumber={} prior={} target={}",
                    adminId, orderNumber, priorStatus, targetStatus);
            throw new BadRequestException(
                    String.format(MSG_INVALID_STATUS_TRANSITION, priorStatus, targetStatus));
        }

        OrderEntity order = orderRepository.findByOrderNumber(orderNumber)
                .orElseThrow(() -> new NotFoundException(MSG_ORDER_NOT_FOUND));

        int affected = orderRepository.updateStatus(order.getId(), priorStatus, targetStatus, adminId);
        if (affected == 0) {
            log.warn("Update status race: orderId={} adminId={} prior={} target={} actual={}",
                    order.getId(), adminId, priorStatus, targetStatus, order.getStatus());
            throw new ConflictException(MSG_ORDER_STATUS_CHANGED);
        }

        log.info("Update status completed: orderId={} adminId={} orderNumber={} prior={} target={}",
                order.getId(), adminId, orderNumber, priorStatus, targetStatus);
    }

    @Transactional
    public void updateShipping(String orderNumber,
                               String priorStatus,
                               ShippingDetailsDto shipping,
                               Long adminId) {
        log.info("Update shipping started: adminId={} orderNumber={} prior={}",
                adminId, orderNumber, priorStatus);

        if (!ADMIN_EDITABLE_SHIPPING_STATUSES.contains(priorStatus)) {
            log.warn("Update shipping rejected (status not editable): adminId={} orderNumber={} prior={}",
                    adminId, orderNumber, priorStatus);
            throw new BadRequestException(MSG_SHIPPING_NOT_EDITABLE);
        }

        OrderEntity order = orderRepository.findByOrderNumber(orderNumber)
                .orElseThrow(() -> new NotFoundException(MSG_ORDER_NOT_FOUND));

        int affected = orderRepository.updateShipping(
                order.getId(),
                priorStatus,
                shipping.getFullName(),
                shipping.getEmail(),
                shipping.getPhone(),
                shipping.getCountry(),
                shipping.getCity(),
                shipping.getAddressLine(),
                adminId);
        if (affected == 0) {
            log.warn("Update shipping race: orderId={} adminId={} prior={} actual={}",
                    order.getId(), adminId, priorStatus, order.getStatus());
            throw new ConflictException(MSG_ORDER_STATUS_CHANGED);
        }

        log.info("Update shipping completed: orderId={} adminId={} orderNumber={} prior={}",
                order.getId(), adminId, orderNumber, priorStatus);
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
