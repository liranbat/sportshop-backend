package com.java.sadna.backend.sportshop.model;

import com.java.sadna.backend.sportshop.model.enums.StockIssueKind;

public class StockIssueDto implements BaseDto {

    private final Long productId;
    private final String productName;
    private final String size;
    private final StockIssueKind kind;
    private final int availableStock;
    private final int requestedQuantity;

    public StockIssueDto(Long productId,
                         String productName,
                         String size,
                         StockIssueKind kind,
                         int availableStock,
                         int requestedQuantity) {
        this.productId = productId;
        this.productName = productName;
        this.size = size;
        this.kind = kind;
        this.availableStock = availableStock;
        this.requestedQuantity = requestedQuantity;
    }

    public Long getProductId() {
        return productId;
    }

    public String getProductName() {
        return productName;
    }

    public String getSize() {
        return size;
    }

    public StockIssueKind getKind() {
        return kind;
    }

    public int getAvailableStock() {
        return availableStock;
    }

    public int getRequestedQuantity() {
        return requestedQuantity;
    }
}
