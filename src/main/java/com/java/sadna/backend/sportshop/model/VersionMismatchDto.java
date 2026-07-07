package com.java.sadna.backend.sportshop.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class VersionMismatchDto {

    private final Long productId;
    private final String productName;
    private final String size;
    private final boolean productIsArchived;
}
