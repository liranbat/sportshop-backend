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
    private final CustomerForOrderDtoToCustomerForOrderMapper customerForOrderDtoToCustomerForOrderMapper;

    public OrderDetailDtoToOrderDetailMapper(OrderItemDtoToOrderItemMapper orderItemDtoToOrderItemMapper,
                                             OrderPaymentDtoToOrderPaymentMapper orderPaymentDtoToOrderPaymentMapper,
                                             CustomerForOrderDtoToCustomerForOrderMapper customerForOrderDtoToCustomerForOrderMapper) {
        this.orderItemDtoToOrderItemMapper = orderItemDtoToOrderItemMapper;
        this.orderPaymentDtoToOrderPaymentMapper = orderPaymentDtoToOrderPaymentMapper;
        this.customerForOrderDtoToCustomerForOrderMapper = customerForOrderDtoToCustomerForOrderMapper;
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
                orderPaymentDtoToOrderPaymentMapper.map(source.getPayment()),
                customerForOrderDtoToCustomerForOrderMapper.map(source.getCustomer())
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
