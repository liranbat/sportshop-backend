package com.java.sadna.backend.sportshop.mapper;

import com.java.sadna.backend.sportshop.entity.OrderItemEntity;
import com.java.sadna.backend.sportshop.model.OrderItemDto;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class OrderItemEntityToOrderItemDtoMapper implements BaseMapper<OrderItemEntity, OrderItemDto> {

    @Override
    public OrderItemDto map(OrderItemEntity entity) {
        BigDecimal lineTotal = entity.getPricePerUnit().multiply(BigDecimal.valueOf(entity.getQuantity()));
        return new OrderItemDto(
                entity.getProductId(),
                entity.getProductName(),
                entity.getProductImageUrl(),
                entity.getSize(),
                entity.getQuantity(),
                entity.getPricePerUnit(),
                lineTotal
        );
    }
}
