package com.java.sadna.backend.sportshop.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class PaymentDetailsDto {

    private final String cardNumber;
    private final String expiry;
    private final String cvv;
    private final String nameOnCard;
}
