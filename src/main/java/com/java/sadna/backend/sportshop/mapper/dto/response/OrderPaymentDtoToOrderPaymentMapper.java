package com.java.sadna.backend.sportshop.mapper.dto.response;

import com.java.sadna.backend.sportshop.api.generated.orders.model.OrderPayment;
import com.java.sadna.backend.sportshop.api.generated.orders.model.PaymentStatus;
import com.java.sadna.backend.sportshop.mapper.BaseMapper;
import com.java.sadna.backend.sportshop.model.OrderPaymentDto;
import org.springframework.stereotype.Component;

@Component
public class OrderPaymentDtoToOrderPaymentMapper implements BaseMapper<OrderPaymentDto, OrderPayment> {

    @Override
    public OrderPayment map(OrderPaymentDto source) {
        return new OrderPayment(
                source.getProvider(),
                source.getTransactionId(),
                source.getAmount(),
                source.getCurrency(),
                PaymentStatus.fromValue(source.getStatus()),
                source.getProcessedAt()
        ).refundedAt(source.getRefundedAt());
    }
}
