package com.java.sadna.backend.sportshop.mapper.dto.response;

import com.java.sadna.backend.sportshop.api.generated.orders.model.OrderItem;
import com.java.sadna.backend.sportshop.mapper.BaseMapper;
import com.java.sadna.backend.sportshop.model.OrderItemDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface OrderItemDtoToOrderItemMapper extends BaseMapper<OrderItemDto, OrderItem> {

    @Override
    OrderItem map(OrderItemDto source);
}
