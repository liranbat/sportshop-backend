package com.java.sadna.backend.sportshop.model;

public class StockIssueDto implements BaseDto {

    private final Long productId;
    private final String productName;
    private final String size;
    private final StockIssueKindDto kind;
    private final int availableStock;
    private final int requestedQuantity;

    public StockIssueDto(Long productId,
                         String productName,
                         String size,
                         StockIssueKindDto kind,
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

    public StockIssueKindDto getKind() {
        return kind;
    }

    public int getAvailableStock() {
        return availableStock;
    }

    public int getRequestedQuantity() {
        return requestedQuantity;
    }
}
