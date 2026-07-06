package com.java.sadna.backend.sportshop.mapper.request.dto;

import com.java.sadna.backend.sportshop.api.generated.checkout.model.PaymentDetails;
import com.java.sadna.backend.sportshop.mapper.BaseMapper;
import com.java.sadna.backend.sportshop.model.PaymentDetailsDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PaymentDetailsToPaymentDetailsDtoMapper extends BaseMapper<PaymentDetails, PaymentDetailsDto> {

    @Override
    PaymentDetailsDto map(PaymentDetails source);
}
