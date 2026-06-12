package com.java.sadna.backend.sportshop.mapper;

import com.java.sadna.backend.sportshop.api.generated.orders.model.OrderStatus;
import com.java.sadna.backend.sportshop.api.generated.orders.model.OrderSummary;
import com.java.sadna.backend.sportshop.model.OrderSummaryDto;
import org.springframework.stereotype.Component;

@Component
public class OrderSummaryDtoToOrderSummaryMapper implements BaseMapper<OrderSummaryDto, OrderSummary> {

    @Override
    public OrderSummary map(OrderSummaryDto source) {
        return new OrderSummary(
                source.getOrderNumber(),
                OrderStatus.fromValue(source.getStatus()),
                source.getCreatedAt(),
                source.getItemCount(),
                source.getTotalPrice()
        );
    }
}
