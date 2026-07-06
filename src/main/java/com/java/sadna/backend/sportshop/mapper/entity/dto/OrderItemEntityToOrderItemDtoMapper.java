package com.java.sadna.backend.sportshop.mapper.entity.dto;

import com.java.sadna.backend.sportshop.entity.OrderItemEntity;
import com.java.sadna.backend.sportshop.mapper.BaseMapper;
import com.java.sadna.backend.sportshop.model.OrderItemDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", imports = java.math.BigDecimal.class)
public interface OrderItemEntityToOrderItemDtoMapper extends BaseMapper<OrderItemEntity, OrderItemDto> {

    @Override
    @Mapping(target = "lineTotal", expression = "java(entity.getPricePerUnit().multiply(BigDecimal.valueOf(entity.getQuantity())))")
    OrderItemDto map(OrderItemEntity entity);
}
