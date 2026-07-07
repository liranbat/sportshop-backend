package com.java.sadna.backend.sportshop.mapper.entity.dto;

import com.java.sadna.backend.sportshop.common.util.PaymentStatuses;
import com.java.sadna.backend.sportshop.entity.PaymentEntity;
import com.java.sadna.backend.sportshop.mapper.BaseMapper;
import com.java.sadna.backend.sportshop.model.OrderPaymentDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", imports = PaymentStatuses.class)
public interface PaymentEntityToOrderPaymentDtoMapper extends BaseMapper<PaymentEntity, OrderPaymentDto> {

    @Override
    @Mapping(source = "createdAt", target = "processedAt")
    @Mapping(target = "refundedAt",
            expression = "java(PaymentStatuses.REFUNDED.equals(entity.getStatus()) ? entity.getUpdatedAt() : null)")
    OrderPaymentDto map(PaymentEntity entity);
}
