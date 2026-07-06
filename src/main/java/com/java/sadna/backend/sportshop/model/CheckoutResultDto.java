package com.java.sadna.backend.sportshop.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;

@Getter
@RequiredArgsConstructor
public class CheckoutResultDto {

    private final String orderNumber;
    private final int itemCount;
    private final BigDecimal totalPrice;
}
