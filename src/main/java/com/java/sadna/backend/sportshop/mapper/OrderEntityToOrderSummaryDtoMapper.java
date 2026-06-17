package com.java.sadna.backend.sportshop.mapper;

import com.java.sadna.backend.sportshop.entity.OrderEntity;
import com.java.sadna.backend.sportshop.model.OrderSummaryDto;
import org.springframework.stereotype.Component;

@Component
public class OrderEntityToOrderSummaryDtoMapper implements BaseMapper<OrderEntity, OrderSummaryDto> {

    private final UserEntityToCustomerForOrderDtoMapper userEntityToCustomerForOrderDtoMapper;

    public OrderEntityToOrderSummaryDtoMapper(UserEntityToCustomerForOrderDtoMapper userEntityToCustomerForOrderDtoMapper) {
        this.userEntityToCustomerForOrderDtoMapper = userEntityToCustomerForOrderDtoMapper;
    }

    @Override
    public OrderSummaryDto map(OrderEntity entity) {
        return new OrderSummaryDto(
                entity.getOrderNumber(),
                entity.getStatus(),
                entity.getCreatedAt(),
                entity.getItemCount(),
                entity.getTotalPrice(),
                userEntityToCustomerForOrderDtoMapper.map(entity.getUser())
        );
    }
}
