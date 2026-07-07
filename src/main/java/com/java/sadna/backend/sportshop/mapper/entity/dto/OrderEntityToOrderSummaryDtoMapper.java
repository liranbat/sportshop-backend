package com.java.sadna.backend.sportshop.mapper.entity.dto;

import com.java.sadna.backend.sportshop.entity.OrderEntity;
import com.java.sadna.backend.sportshop.mapper.BaseMapper;
import com.java.sadna.backend.sportshop.model.OrderSummaryDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = UserEntityToCustomerForOrderDtoMapper.class)
public interface OrderEntityToOrderSummaryDtoMapper extends BaseMapper<OrderEntity, OrderSummaryDto> {

    @Override
    @Mapping(source = "user", target = "customer")
    OrderSummaryDto map(OrderEntity entity);
}
