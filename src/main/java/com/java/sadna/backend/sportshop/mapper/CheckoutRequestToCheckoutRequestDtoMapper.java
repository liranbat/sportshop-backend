package com.java.sadna.backend.sportshop.mapper;

import com.java.sadna.backend.sportshop.api.generated.checkout.model.CheckoutRequest;
import com.java.sadna.backend.sportshop.api.generated.checkout.model.PaymentDetails;
import com.java.sadna.backend.sportshop.api.generated.checkout.model.ShippingDetails;
import com.java.sadna.backend.sportshop.model.CheckoutRequestDto;
import com.java.sadna.backend.sportshop.model.PaymentDetailsDto;
import com.java.sadna.backend.sportshop.model.ShippingDetailsDto;
import org.springframework.stereotype.Component;

@Component
public class CheckoutRequestToCheckoutRequestDtoMapper implements BaseMapper<CheckoutRequest, CheckoutRequestDto> {

    @Override
    public CheckoutRequestDto map(CheckoutRequest source) {
        ShippingDetails shipping = source.getShipping();
        PaymentDetails payment = source.getPayment();
        return new CheckoutRequestDto(
                new ShippingDetailsDto(
                        shipping.getFullName(),
                        shipping.getEmail(),
                        shipping.getPhone(),
                        shipping.getCountry(),
                        shipping.getCity(),
                        shipping.getAddressLine()
                ),
                new PaymentDetailsDto(
                        payment.getCardNumber(),
                        payment.getExpiry(),
                        payment.getCvv(),
                        payment.getNameOnCard()
                )
        );
    }
}
