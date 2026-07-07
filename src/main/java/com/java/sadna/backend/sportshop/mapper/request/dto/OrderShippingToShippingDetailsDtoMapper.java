package com.java.sadna.backend.sportshop.mapper.request.dto;

import com.java.sadna.backend.sportshop.api.generated.orders.model.OrderShipping;
import com.java.sadna.backend.sportshop.mapper.BaseMapper;
import com.java.sadna.backend.sportshop.model.ShippingDetailsDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface OrderShippingToShippingDetailsDtoMapper extends BaseMapper<OrderShipping, ShippingDetailsDto> {

    @Override
    ShippingDetailsDto map(OrderShipping source);
}
