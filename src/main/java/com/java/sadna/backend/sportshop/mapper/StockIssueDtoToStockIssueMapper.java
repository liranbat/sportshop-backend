package com.java.sadna.backend.sportshop.mapper;

import com.java.sadna.backend.sportshop.api.generated.cart.model.StockIssue;
import com.java.sadna.backend.sportshop.api.generated.cart.model.StockIssueKind;
import com.java.sadna.backend.sportshop.model.StockIssueDto;
import com.java.sadna.backend.sportshop.model.StockIssueKindDto;
import org.springframework.stereotype.Component;

@Component
public class StockIssueDtoToStockIssueMapper implements BaseMapper<StockIssueDto, StockIssue> {

    @Override
    public StockIssue map(StockIssueDto source) {
        return new StockIssue()
                .productId(source.getProductId())
                .productName(source.getProductName())
                .size(source.getSize())
                .kind(mapKind(source.getKind()))
                .availableStock(source.getAvailableStock())
                .requestedQuantity(source.getRequestedQuantity());
    }

    private static StockIssueKind mapKind(StockIssueKindDto kind) {
        return switch (kind) {
            case OUT_OF_STOCK -> StockIssueKind.OUT_OF_STOCK;
            case INSUFFICIENT_STOCK -> StockIssueKind.INSUFFICIENT_STOCK;
        };
    }
}
