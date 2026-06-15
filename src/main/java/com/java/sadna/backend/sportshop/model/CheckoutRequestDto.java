package com.java.sadna.backend.sportshop.model;

public class CheckoutRequestDto implements BaseDto {

    private final ShippingDetailsDto shipping;
    private final PaymentDetailsDto payment;

    public CheckoutRequestDto(ShippingDetailsDto shipping, PaymentDetailsDto payment) {
        this.shipping = shipping;
        this.payment = payment;
    }

    public ShippingDetailsDto getShipping() {
        return shipping;
    }

    public PaymentDetailsDto getPayment() {
        return payment;
    }
}
