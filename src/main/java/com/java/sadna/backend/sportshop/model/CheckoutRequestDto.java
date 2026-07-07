package com.java.sadna.backend.sportshop.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class CheckoutRequestDto {

    private final ShippingDetailsDto shipping;
    private final PaymentDetailsDto payment;
}
