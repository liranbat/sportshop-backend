package com.java.sadna.backend.sportshop.mapper;

import com.java.sadna.backend.sportshop.entity.PaymentEntity;
import com.java.sadna.backend.sportshop.model.OrderPaymentDto;
import org.springframework.stereotype.Component;

@Component
public class PaymentEntityToOrderPaymentDtoMapper implements BaseMapper<PaymentEntity, OrderPaymentDto> {

    private static final String STATUS_REFUNDED = "REFUNDED";

    @Override
    public OrderPaymentDto map(PaymentEntity entity) {
        boolean isRefunded = STATUS_REFUNDED.equals(entity.getStatus());
        return new OrderPaymentDto(
                entity.getProvider(),
                entity.getTransactionId(),
                entity.getAmount(),
                entity.getCurrency(),
                entity.getStatus(),
                entity.getCreatedAt(),
                isRefunded ? entity.getUpdatedAt() : null
        );
    }
}
