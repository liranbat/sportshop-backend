package com.java.sadna.backend.sportshop.mapper.dto.response;

import com.java.sadna.backend.sportshop.api.generated.orders.model.OrderStatus;
import com.java.sadna.backend.sportshop.api.generated.orders.model.OrderSummary;
import com.java.sadna.backend.sportshop.mapper.BaseMapper;
import com.java.sadna.backend.sportshop.model.OrderSummaryDto;
import org.springframework.stereotype.Component;

@Component
public class OrderSummaryDtoToOrderSummaryMapper implements BaseMapper<OrderSummaryDto, OrderSummary> {

    private final CustomerForOrderDtoToCustomerForOrderMapper customerForOrderDtoToCustomerForOrderMapper;

    public OrderSummaryDtoToOrderSummaryMapper(CustomerForOrderDtoToCustomerForOrderMapper customerForOrderDtoToCustomerForOrderMapper) {
        this.customerForOrderDtoToCustomerForOrderMapper = customerForOrderDtoToCustomerForOrderMapper;
    }

    @Override
    public OrderSummary map(OrderSummaryDto source) {
        return new OrderSummary(
                source.getOrderNumber(),
                OrderStatus.fromValue(source.getStatus()),
                source.getCreatedAt(),
                source.getItemCount(),
                source.getTotalPrice(),
                customerForOrderDtoToCustomerForOrderMapper.map(source.getCustomer())
        );
    }
}
