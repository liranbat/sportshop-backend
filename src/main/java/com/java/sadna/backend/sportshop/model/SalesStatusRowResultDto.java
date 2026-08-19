package com.java.sadna.backend.sportshop.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;

@Getter
@RequiredArgsConstructor
public class SalesStatusRowResultDto {

    private final String status;
    private final Long orderCount;
    private final BigDecimal revenue;
}
