package com.java.sadna.backend.sportshop.mapper;

import com.java.sadna.backend.sportshop.api.generated.orders.model.OrderItem;
import com.java.sadna.backend.sportshop.model.OrderItemDto;
import org.springframework.stereotype.Component;

@Component
public class OrderItemDtoToOrderItemMapper implements BaseMapper<OrderItemDto, OrderItem> {

    @Override
    public OrderItem map(OrderItemDto source) {
        return new OrderItem(
                source.getProductId(),
                source.getProductName(),
                source.getSize(),
                source.getQuantity(),
                source.getPricePerUnit(),
                source.getLineTotal()
        ).productImageUrl(source.getProductImageUrl());
    }
}
