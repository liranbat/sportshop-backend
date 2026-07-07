package com.java.sadna.backend.sportshop.mapper.dto.response;

import com.java.sadna.backend.sportshop.api.generated.orders.model.OrderStatus;
import com.java.sadna.backend.sportshop.api.generated.orders.model.OrderSummary;
import com.java.sadna.backend.sportshop.mapper.BaseMapper;
import com.java.sadna.backend.sportshop.model.OrderSummaryDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring",
        uses = CustomerForOrderDtoToCustomerForOrderMapper.class,
        imports = OrderStatus.class)
public interface OrderSummaryDtoToOrderSummaryMapper extends BaseMapper<OrderSummaryDto, OrderSummary> {

    @Override
    @Mapping(target = "status", expression = "java(OrderStatus.fromValue(source.getStatus()))")
    OrderSummary map(OrderSummaryDto source);
}
