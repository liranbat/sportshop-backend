package com.java.sadna.backend.sportshop.mapper.dto.response;

import com.java.sadna.backend.sportshop.api.generated.orders.model.OrderPayment;
import com.java.sadna.backend.sportshop.api.generated.orders.model.PaymentStatus;
import com.java.sadna.backend.sportshop.mapper.BaseMapper;
import com.java.sadna.backend.sportshop.model.OrderPaymentDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", imports = PaymentStatus.class)
public interface OrderPaymentDtoToOrderPaymentMapper extends BaseMapper<OrderPaymentDto, OrderPayment> {

    @Override
    @Mapping(target = "status", expression = "java(PaymentStatus.fromValue(source.getStatus()))")
    OrderPayment map(OrderPaymentDto source);
}
