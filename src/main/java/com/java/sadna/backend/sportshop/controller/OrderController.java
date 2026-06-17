package com.java.sadna.backend.sportshop.controller;

import com.java.sadna.backend.sportshop.api.generated.orders.api.AdminOrdersApi;
import com.java.sadna.backend.sportshop.api.generated.orders.api.OrdersApi;
import com.java.sadna.backend.sportshop.api.generated.orders.model.OrderDetail;
import com.java.sadna.backend.sportshop.api.generated.orders.model.OrderListPage;
import com.java.sadna.backend.sportshop.api.generated.orders.model.OrderStatus;
import com.java.sadna.backend.sportshop.api.generated.orders.model.UpdateOrderStatusRequest;
import com.java.sadna.backend.sportshop.mapper.OrderDetailDtoToOrderDetailMapper;
import com.java.sadna.backend.sportshop.mapper.PagedOrderSummaryDtoToOrderListPageMapper;
import com.java.sadna.backend.sportshop.model.OrderDetailDto;
import com.java.sadna.backend.sportshop.model.OrderSummaryDto;
import com.java.sadna.backend.sportshop.model.PagedResult;
import com.java.sadna.backend.sportshop.security.SecurityContextUtils;
import com.java.sadna.backend.sportshop.service.OrderService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.time.LocalDate;

@RestController
public class OrderController implements OrdersApi, AdminOrdersApi {

    private final OrderService orderService;
    private final PagedOrderSummaryDtoToOrderListPageMapper pagedOrderSummaryDtoToOrderListPageMapper;
    private final OrderDetailDtoToOrderDetailMapper orderDetailDtoToOrderDetailMapper;

    public OrderController(OrderService orderService,
                           PagedOrderSummaryDtoToOrderListPageMapper pagedOrderSummaryDtoToOrderListPageMapper,
                           OrderDetailDtoToOrderDetailMapper orderDetailDtoToOrderDetailMapper) {
        this.orderService = orderService;
        this.pagedOrderSummaryDtoToOrderListPageMapper = pagedOrderSummaryDtoToOrderListPageMapper;
        this.orderDetailDtoToOrderDetailMapper = orderDetailDtoToOrderDetailMapper;
    }

    @Override
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<OrderListPage> listOrders(OrderStatus status,
                                                    String orderNumber,
                                                    BigDecimal amountMin,
                                                    BigDecimal amountMax,
                                                    LocalDate dateFrom,
                                                    LocalDate dateTo,
                                                    String sortField,
                                                    String sortDirection,
                                                    Integer page,
                                                    Integer pageSize) {
        Long userId = SecurityContextUtils.currentUserIdOrThrow();
        PagedResult<OrderSummaryDto> result = orderService.list(
                userId,
                status == null ? null : status.getValue(),
                orderNumber,
                null,
                amountMin,
                amountMax,
                dateFrom,
                dateTo,
                sortField,
                sortDirection,
                page,
                pageSize
        );
        return ResponseEntity.ok(pagedOrderSummaryDtoToOrderListPageMapper.map(result));
    }

    @Override
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<OrderDetail> getOrder(String orderNumber) {
        Long userId = SecurityContextUtils.currentUserIdOrThrow();
        OrderDetailDto detail = orderService.getDetail(orderNumber, userId);
        return ResponseEntity.ok(orderDetailDtoToOrderDetailMapper.map(detail));
    }

    @Override
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> cancelOrder(String orderNumber) {
        Long userId = SecurityContextUtils.currentUserIdOrThrow();
        orderService.cancel(orderNumber, userId, userId);
        return ResponseEntity.noContent().build();
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<OrderListPage> listAdminOrders(OrderStatus status,
                                                         String orderNumber,
                                                         String customer,
                                                         BigDecimal amountMin,
                                                         BigDecimal amountMax,
                                                         LocalDate dateFrom,
                                                         LocalDate dateTo,
                                                         String sortField,
                                                         String sortDirection,
                                                         Integer page,
                                                         Integer pageSize) {
        PagedResult<OrderSummaryDto> result = orderService.list(
                null,
                status == null ? null : status.getValue(),
                orderNumber,
                customer,
                amountMin,
                amountMax,
                dateFrom,
                dateTo,
                sortField,
                sortDirection,
                page,
                pageSize
        );
        return ResponseEntity.ok(pagedOrderSummaryDtoToOrderListPageMapper.map(result));
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<OrderDetail> getAdminOrder(String orderNumber) {
        OrderDetailDto detail = orderService.getDetail(orderNumber, null);
        return ResponseEntity.ok(orderDetailDtoToOrderDetailMapper.map(detail));
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> cancelAdminOrder(String orderNumber) {
        Long adminId = SecurityContextUtils.currentUserIdOrThrow();
        orderService.cancel(orderNumber, null, adminId);
        return ResponseEntity.noContent().build();
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> updateAdminOrderStatus(String orderNumber, UpdateOrderStatusRequest body) {
        Long adminId = SecurityContextUtils.currentUserIdOrThrow();
        orderService.updateStatus(
                orderNumber,
                body.getPriorStatus().getValue(),
                body.getTargetStatus().getValue(),
                adminId
        );
        return ResponseEntity.noContent().build();
    }
}
