package com.java.sadna.backend.sportshop.model;

import java.util.List;

public class CartValidationResultDto implements BaseDto {

    private final boolean ok;
    private final List<VersionMismatchDto> versionMismatches;
    private final List<StockIssueDto> stockIssues;
    private final CartViewDto cart;

    public CartValidationResultDto(boolean ok,
                                   List<VersionMismatchDto> versionMismatches,
                                   List<StockIssueDto> stockIssues,
                                   CartViewDto cart) {
        this.ok = ok;
        this.versionMismatches = versionMismatches;
        this.stockIssues = stockIssues;
        this.cart = cart;
    }

    public boolean isOk() {
        return ok;
    }

    public List<VersionMismatchDto> getVersionMismatches() {
        return versionMismatches;
    }

    public List<StockIssueDto> getStockIssues() {
        return stockIssues;
    }

    public CartViewDto getCart() {
        return cart;
    }
}
