package com.java.sadna.backend.sportshop.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Getter
@RequiredArgsConstructor
public class CartViewDto {

    private final List<CartItemDto> items;
    private final int itemCount;
    private final BigDecimal subtotal;
}
