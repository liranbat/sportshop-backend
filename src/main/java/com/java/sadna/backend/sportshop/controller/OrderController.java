package com.java.sadna.backend.sportshop.controller;

import com.java.sadna.backend.sportshop.api.generated.orders.api.OrdersApi;
import com.java.sadna.backend.sportshop.api.generated.orders.model.OrderDetail;
import com.java.sadna.backend.sportshop.api.generated.orders.model.OrderListPage;
import com.java.sadna.backend.sportshop.api.generated.orders.model.OrderStatus;
import com.java.sadna.backend.sportshop.mapper.PagedOrderSummaryDtoToOrderListPageMapper;
import com.java.sadna.backend.sportshop.model.OrderSummaryDto;
import com.java.sadna.backend.sportshop.model.PagedResult;
import com.java.sadna.backend.sportshop.security.SecurityContextUtils;
import com.java.sadna.backend.sportshop.service.OrderService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.time.LocalDate;

@RestController
public class OrderController implements OrdersApi {

    private final OrderService orderService;
    private final PagedOrderSummaryDtoToOrderListPageMapper pagedOrderSummaryDtoToOrderListPageMapper;

    public OrderController(OrderService orderService,
                           PagedOrderSummaryDtoToOrderListPageMapper pagedOrderSummaryDtoToOrderListPageMapper) {
        this.orderService = orderService;
        this.pagedOrderSummaryDtoToOrderListPageMapper = pagedOrderSummaryDtoToOrderListPageMapper;
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
        PagedResult<OrderSummaryDto> result = orderService.listForUser(
                userId,
                status == null ? null : status.getValue(),
                orderNumber,
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

    // Stubs -- wired in Steps 2.1 / 2.2. skipDefaultInterface=true forces implementation here.
    @Override
    public ResponseEntity<OrderDetail> getOrder(String orderNumber) {
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
    }

    @Override
    public ResponseEntity<OrderDetail> cancelOrder(String orderNumber) {
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
    }
}
