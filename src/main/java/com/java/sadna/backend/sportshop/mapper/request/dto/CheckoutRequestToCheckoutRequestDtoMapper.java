package com.java.sadna.backend.sportshop.mapper.request.dto;

import com.java.sadna.backend.sportshop.api.generated.checkout.model.CheckoutRequest;
import com.java.sadna.backend.sportshop.mapper.BaseMapper;
import com.java.sadna.backend.sportshop.model.CheckoutRequestDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = {
        ShippingDetailsToShippingDetailsDtoMapper.class,
        PaymentDetailsToPaymentDetailsDtoMapper.class
})
public interface CheckoutRequestToCheckoutRequestDtoMapper extends BaseMapper<CheckoutRequest, CheckoutRequestDto> {

    @Override
    CheckoutRequestDto map(CheckoutRequest source);
}
