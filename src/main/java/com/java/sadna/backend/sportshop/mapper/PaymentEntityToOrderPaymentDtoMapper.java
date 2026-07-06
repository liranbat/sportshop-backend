package com.java.sadna.backend.sportshop.mapper;

import com.java.sadna.backend.sportshop.common.util.PaymentStatuses;
import com.java.sadna.backend.sportshop.entity.PaymentEntity;
import com.java.sadna.backend.sportshop.model.OrderPaymentDto;
import org.springframework.stereotype.Component;

@Component
public class PaymentEntityToOrderPaymentDtoMapper implements BaseMapper<PaymentEntity, OrderPaymentDto> {

    @Override
    public OrderPaymentDto map(PaymentEntity entity) {
        boolean isRefunded = PaymentStatuses.REFUNDED.equals(entity.getStatus());
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
