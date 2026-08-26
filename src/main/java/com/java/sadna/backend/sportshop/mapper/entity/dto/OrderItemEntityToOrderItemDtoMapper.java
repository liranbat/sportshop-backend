package com.java.sadna.backend.sportshop.mapper.entity.dto;

import com.java.sadna.backend.sportshop.entity.OrderItemEntity;
import com.java.sadna.backend.sportshop.mapper.BaseMapper;
import com.java.sadna.backend.sportshop.model.OrderItemDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", imports = {
        com.java.sadna.backend.sportshop.common.util.MoneyUtil.class
})
public interface OrderItemEntityToOrderItemDtoMapper extends BaseMapper<OrderItemEntity, OrderItemDto> {

    @Override
    @Mapping(target = "lineTotal", expression = "java(MoneyUtil.lineTotal(entity.getPricePerUnit(), entity.getQuantity()))")
    OrderItemDto map(OrderItemEntity entity);
}
