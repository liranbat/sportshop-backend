package com.java.sadna.backend.sportshop.mapper;

import com.java.sadna.backend.sportshop.api.generated.orders.model.OrderListPage;
import com.java.sadna.backend.sportshop.api.generated.orders.model.OrderSummary;
import com.java.sadna.backend.sportshop.model.OrderSummaryDto;
import com.java.sadna.backend.sportshop.model.PagedResult;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class PagedOrderSummaryDtoToOrderListPageMapper
        implements BaseMapper<PagedResult<OrderSummaryDto>, OrderListPage> {

    private final OrderSummaryDtoToOrderSummaryMapper orderSummaryDtoToOrderSummaryMapper;

    public PagedOrderSummaryDtoToOrderListPageMapper(
            OrderSummaryDtoToOrderSummaryMapper orderSummaryDtoToOrderSummaryMapper) {
        this.orderSummaryDtoToOrderSummaryMapper = orderSummaryDtoToOrderSummaryMapper;
    }

    @Override
    public OrderListPage map(PagedResult<OrderSummaryDto> source) {
        List<OrderSummary> items = source.getItems().stream()
                .map(orderSummaryDtoToOrderSummaryMapper::map)
                .toList();
        return new OrderListPage(
                items,
                source.getPage(),
                source.getPageSize(),
                source.getTotalElements(),
                source.getTotalPages()
        );
    }
}
