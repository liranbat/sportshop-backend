package com.java.sadna.backend.sportshop.mapper;

import com.java.sadna.backend.sportshop.api.generated.orders.model.OrderDetail;
import com.java.sadna.backend.sportshop.api.generated.orders.model.OrderItem;
import com.java.sadna.backend.sportshop.api.generated.orders.model.OrderShipping;
import com.java.sadna.backend.sportshop.api.generated.orders.model.OrderStatus;
import com.java.sadna.backend.sportshop.model.OrderDetailDto;
import com.java.sadna.backend.sportshop.model.ShippingDetailsDto;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class OrderDetailDtoToOrderDetailMapper implements BaseMapper<OrderDetailDto, OrderDetail> {

    private final OrderItemDtoToOrderItemMapper orderItemDtoToOrderItemMapper;
    private final OrderPaymentDtoToOrderPaymentMapper orderPaymentDtoToOrderPaymentMapper;

    public OrderDetailDtoToOrderDetailMapper(OrderItemDtoToOrderItemMapper orderItemDtoToOrderItemMapper,
                                             OrderPaymentDtoToOrderPaymentMapper orderPaymentDtoToOrderPaymentMapper) {
        this.orderItemDtoToOrderItemMapper = orderItemDtoToOrderItemMapper;
        this.orderPaymentDtoToOrderPaymentMapper = orderPaymentDtoToOrderPaymentMapper;
    }

    @Override
    public OrderDetail map(OrderDetailDto source) {
        List<OrderItem> items = source.getItems().stream()
                .map(orderItemDtoToOrderItemMapper::map)
                .toList();
        return new OrderDetail(
                source.getOrderNumber(),
                OrderStatus.fromValue(source.getStatus()),
                source.getCreatedAt(),
                source.getTotalPrice(),
                source.getItemCount(),
                items,
                mapShipping(source.getShipping()),
                orderPaymentDtoToOrderPaymentMapper.map(source.getPayment())
        ).cancelledAt(source.getCancelledAt());
    }

    private OrderShipping mapShipping(ShippingDetailsDto shipping) {
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
