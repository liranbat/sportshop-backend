package com.java.sadna.backend.sportshop.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;

@Getter
@RequiredArgsConstructor
public class ProductUpdateRequestDto {

    private final String name;
    private final String description;
    private final Long categoryId;
    private final boolean multiSize;
    private final String imageUrl;
    private final BigDecimal price;
    private final int version;
}
