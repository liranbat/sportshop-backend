package com.java.sadna.backend.sportshop.model;

import com.java.sadna.backend.sportshop.model.enums.StockIssueKind;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class StockIssueDto {

    private final Long productId;
    private final String productName;
    private final String size;
    private final StockIssueKind kind;
    private final int availableStock;
    private final int requestedQuantity;
}
