package com.java.sadna.backend.sportshop.mapper.dto.response;

import com.java.sadna.backend.sportshop.api.generated.orders.model.OrderDetail;
import com.java.sadna.backend.sportshop.api.generated.orders.model.OrderShipping;
import com.java.sadna.backend.sportshop.api.generated.orders.model.OrderStatus;
import com.java.sadna.backend.sportshop.mapper.BaseMapper;
import com.java.sadna.backend.sportshop.model.OrderDetailDto;
import com.java.sadna.backend.sportshop.model.ShippingDetailsDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring",
        uses = {
                OrderItemDtoToOrderItemMapper.class,
                OrderPaymentDtoToOrderPaymentMapper.class,
                CustomerForOrderDtoToCustomerForOrderMapper.class
        },
        imports = OrderStatus.class)
public interface OrderDetailDtoToOrderDetailMapper extends BaseMapper<OrderDetailDto, OrderDetail> {

    @Override
    @Mapping(target = "status", expression = "java(OrderStatus.fromValue(source.getStatus()))")
    OrderDetail map(OrderDetailDto source);

    default OrderShipping mapShipping(ShippingDetailsDto shipping) {
        if (shipping == null) {
            return null;
        }
        return new OrderShipping(
                shipping.getFullName(),
                shipping.getEmail(),
                shipping.getPhone(),
                shipping.getCountry(),
                shipping.getCity(),
                shipping.getAddressLine()
        );
    }
}
